package id.rsdiz.rdshop.domain.repository.order

import androidx.paging.*
import id.rsdiz.rdshop.base.utils.AppExecutors
import id.rsdiz.rdshop.data.NetworkBoundResource
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.data.paging.OrderRemoteMediator
import id.rsdiz.rdshop.data.source.local.OrderLocalDataSource
import id.rsdiz.rdshop.data.source.remote.OrderRemoteDataSource
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import id.rsdiz.rdshop.data.source.remote.response.order.OrderResponse
import id.rsdiz.rdshop.data.source.remote.response.order.OrdersResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IOrderRepository] from Domain Layer
 */
@Singleton
class OrderRepository @Inject constructor(
    private val remoteDataSource: OrderRemoteDataSource,
    private val localDataSource: OrderLocalDataSource,
    private val appExecutors: AppExecutors
) : IOrderRepository {

    override suspend fun count(): Resource<Int> =
        when (
            val response = remoteDataSource.countOrders().first()
        ) {
            is ApiResponse.Success -> Resource.Success(response.data)
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun getOrders(status: Short?): Flow<PagingData<Order>> = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = OrderRemoteMediator(
            apiService = remoteDataSource.apiService,
            orderDao = localDataSource.orderDao,
            detailOrderDao = localDataSource.detailOrderDao,
            orderRemoteKeysDao = localDataSource.orderRemoteKeysDao,
            mapper = remoteDataSource.mapper,
            status = status
        ),
        pagingSourceFactory = {
            if (status == null) {
                localDataSource.getAllOrder()
            } else {
                localDataSource.getOrderByStatus(status)
            }
        }
    ).flow.map { pagingData ->
        pagingData.map { orderWithDetail ->
            localDataSource.mapper.mapFromEntity(orderWithDetail)
        }
    }

    override fun getNewestOrders(): Flow<Resource<List<Order>>> =
        object : NetworkBoundResource<List<Order>, OrdersResponse>() {
            override fun loadFromDB(): Flow<List<Order>?> =
                localDataSource.getNewestOrder().map {
                    localDataSource.mapper.mapFromEntities(it)
                }

            override fun shouldFetch(data: List<Order>?): Boolean = true

            override suspend fun createCall(): Flow<ApiResponse<OrdersResponse>> =
                remoteDataSource.getOrders()

            override suspend fun saveCallResult(data: OrdersResponse) =
                remoteDataSource.mapper.mapRemoteToEntities(data.results).let {
                    localDataSource.insertAll(it)
                }
        }.asFlow() as Flow<Resource<List<Order>>>

    override fun getOrder(orderId: String): Flow<Resource<Order>> =
        object : NetworkBoundResource<Order, OrderResponse>() {
            override fun loadFromDB(): Flow<Order?> =
                localDataSource.getOrderById(orderId = orderId).map {
                    localDataSource.mapper.mapFromEntity(it)
                }

            override fun shouldFetch(data: Order?): Boolean = data?.orderId.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<OrderResponse>> =
                remoteDataSource.getOrderById(orderId = orderId)

            override suspend fun saveCallResult(data: OrderResponse) =
                remoteDataSource.mapper.mapRemoteToEntity(data).let {
                    localDataSource.insert(it)
                }
        }.asFlow() as Flow<Resource<Order>>

    override fun getOrderByUserId(userId: String): Flow<Resource<List<Order>>> =
        object : NetworkBoundResource<List<Order>, OrdersResponse>() {
            override fun loadFromDB(): Flow<List<Order>?> =
                localDataSource.getOrderByUserId(userId = userId).map {
                    localDataSource.mapper.mapFromEntities(it)
                }

            override fun shouldFetch(data: List<Order>?): Boolean = data.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<OrdersResponse>> =
                remoteDataSource.getOrderByUserId(userId = userId)

            override suspend fun saveCallResult(data: OrdersResponse) =
                remoteDataSource.mapper.mapRemoteToEntities(data.results).let {
                    localDataSource.insertAll(it)
                }
        }.asFlow() as Flow<Resource<List<Order>>>

    override fun getOrderByDate(
        startDate: OffsetDateTime?,
        endDate: OffsetDateTime?
    ): Flow<Resource<List<Order>>> =
        object : NetworkBoundResource<List<Order>, OrdersResponse>() {
            override fun loadFromDB(): Flow<List<Order>?> =
                localDataSource.getOrderByDate(startDate, endDate).map {
                    localDataSource.mapper.mapFromEntities(it)
                }

            override fun shouldFetch(data: List<Order>?): Boolean = data.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<OrdersResponse>> =
                remoteDataSource.getOrderByDate(startDate, endDate)

            override suspend fun saveCallResult(data: OrdersResponse) =
                remoteDataSource.mapper.mapRemoteToEntities(data.results).let {
                    localDataSource.insertAll(it)
                }
        }.asFlow() as Flow<Resource<List<Order>>>

    override suspend fun insertOrder(order: Order): Resource<String> =
        when (val response = remoteDataSource.createOrder(order = order).first()) {
            is ApiResponse.Success -> {
                remoteDataSource.mapper.mapRemoteToEntity(response.data).let {
                    localDataSource.insert(it)
                }

                Resource.Success("Order Successfully Added!")
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun updateOrder(
        orderId: String,
        status: Short,
        trackingNumber: String
    ): Resource<String> =
        when (
            val response = remoteDataSource.updateOrder(
                orderId = orderId,
                status = status,
                trackingNumber = trackingNumber
            ).first()
        ) {
            is ApiResponse.Success -> {
                remoteDataSource.mapper.mapRemoteToEntity(response.data).let {
                    appExecutors.diskIO().execute {
                        localDataSource.update(it.order)
                    }
                }

                Resource.Success("Order Successfully Updated!")
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }
}
