package id.rsdiz.rdshop.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import id.rsdiz.rdshop.data.source.local.entity.ProductEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductRemoteKeysEntity
import id.rsdiz.rdshop.data.source.local.room.IProductDao
import id.rsdiz.rdshop.data.source.local.room.IProductImageDao
import id.rsdiz.rdshop.data.source.local.room.IProductRemoteKeysDao
import id.rsdiz.rdshop.data.source.remote.mapper.ProductRemoteMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import id.rsdiz.rdshop.domain.model.Product

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val apiService: ApiService,
    private val productDao: IProductDao,
    private val productImageDao: IProductImageDao,
    private val productRemoteKeysDao: IProductRemoteKeysDao,
    private val mapper: ProductRemoteMapper
) : RemoteMediator<Int, Product>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Product>
    ): MediatorResult {
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

            val response = apiService.getProducts(page = page)
            var endOfPaginationReached = false

            if (response.code == 200) {
                val responseData = response.data
                endOfPaginationReached = responseData == null
                responseData?.let { data ->
                    if (loadType == LoadType.REFRESH) {
                        productDao.deleteAll()
                        productRemoteKeysDao.deleteAll()
                    }
                    var previous: Int? = null
                    val next: Int = page + 1

                    data.previous?.let {
                        previous = if (page <= 1) null else page - 1
                    }

                    val keys = data.results.map { product ->
                        ProductRemoteKeysEntity(
                            id = product.id,
                            previous = previous,
                            next = next,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }

                    productRemoteKeysDao.insertAll(keys)

                    val productList = mutableListOf<ProductEntity>()
                    mapper.mapRemoteToEntities(data.results).map {
                        productList.add(it.product)
                        productImageDao.insertAll(it.images)
                    }
                    productDao.insertAll(productList)
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Product>
    ): ProductRemoteKeysEntity? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.productId?.let {
                productRemoteKeysDao.getProductRemoteKeys(productId = it)
            }
        }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, Product>
    ): ProductRemoteKeysEntity? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { product ->
            productRemoteKeysDao.getProductRemoteKeys(productId = product.productId)
        }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, Product>
    ): ProductRemoteKeysEntity? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { product ->
            productRemoteKeysDao.getProductRemoteKeys(productId = product.productId)
        }
}
