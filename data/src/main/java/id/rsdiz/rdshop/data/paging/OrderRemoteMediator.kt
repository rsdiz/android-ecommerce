package id.rsdiz.rdshop.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import id.rsdiz.rdshop.data.source.local.entity.OrderEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderRemoteKeysEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderWithDetails
import id.rsdiz.rdshop.data.source.local.room.IDetailOrderDao
import id.rsdiz.rdshop.data.source.local.room.IOrderDao
import id.rsdiz.rdshop.data.source.local.room.IOrderRemoteKeysDao
import id.rsdiz.rdshop.data.source.remote.mapper.OrderRemoteMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import id.rsdiz.rdshop.domain.model.Order

@OptIn(ExperimentalPagingApi::class)
class OrderRemoteMediator(
    private val apiService: ApiService,
    private val orderDao: IOrderDao,
    private val detailOrderDao: IDetailOrderDao,
    private val orderRemoteKeysDao: IOrderRemoteKeysDao,
    private val mapper: OrderRemoteMapper
) : RemoteMediator<Int, OrderWithDetails>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, OrderWithDetails>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.next?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val previous = remoteKeys?.previous
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    previous
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val next = remoteKeys?.next
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    next
                }
            }

            val response = apiService.getOrders(page = page)
            var endOfPaginationReached = false

            if (response.code == 200) {
                val responseData = response.data
                endOfPaginationReached = responseData == null
                responseData?.let { data ->
                    if (loadType == LoadType.REFRESH) {
                        orderDao.deleteAll()
                        orderRemoteKeysDao.deleteAll()
                    }
                    var previous: Int? = null
                    var next: Int? = null

                    data.next?.let {
                        next = page + 1
                    }

                    data.previous?.let {
                        previous = if (page <= 1) null else page - 1
                    }

                    val keys = data.results.map { order ->
                        OrderRemoteKeysEntity(
                            id = order.id,
                            previous = previous,
                            next = next,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }

                    orderRemoteKeysDao.insertAll(keys)

                    val orderList = mutableListOf<OrderEntity>()
                    mapper.mapRemoteToEntities(data.results).map {
                        orderList.add(it.order)
                        detailOrderDao.insertAll(it.details)
                    }
                    orderDao.insertAll(orderList)
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, OrderWithDetails>
    ): OrderRemoteKeysEntity? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.order?.orderId?.let {
                orderRemoteKeysDao.getOrderRemoteKeys(orderId = it)
            }
        }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, OrderWithDetails>
    ): OrderRemoteKeysEntity? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { orderWithDetails ->
            orderRemoteKeysDao.getOrderRemoteKeys(orderId = orderWithDetails.order.orderId)
        }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, OrderWithDetails>
    ): OrderRemoteKeysEntity? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { orderWithDetails ->
            orderRemoteKeysDao.getOrderRemoteKeys(orderId = orderWithDetails.order.orderId)
        }
}
