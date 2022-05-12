package id.rsdiz.rdshop.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import id.rsdiz.rdshop.data.source.local.entity.ProductRemoteKeysEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductWithImages
import id.rsdiz.rdshop.data.source.local.room.IProductDao
import id.rsdiz.rdshop.data.source.local.room.IProductImageDao
import id.rsdiz.rdshop.data.source.local.room.IProductRemoteKeysDao
import id.rsdiz.rdshop.data.source.remote.mapper.ProductRemoteMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val apiService: ApiService,
    private val productDao: IProductDao,
    private val productImageDao: IProductImageDao,
    private val productRemoteKeysDao: IProductRemoteKeysDao,
    private val mapper: ProductRemoteMapper
) : RemoteMediator<Int, ProductWithImages>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductWithImages>
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

                    val oldData = mutableListOf<ProductWithImages>()
                    if (loadType == LoadType.REFRESH) {
                        mapper.mapRemoteToEntities(data.results).map {
                            val current = productDao.getProductById(it.product.productId).first()
                            current?.let { oldData.add(current) }
                        }

                        productDao.deleteAll()
                        productRemoteKeysDao.deleteAll()
                    }

                    var previous: Int? = null
                    var next: Int? = null

                    data.next?.let {
                        next = page + 1
                    }

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

                    mapper.mapRemoteToEntities(data.results).map {
                        val currentData =
                            if (loadType != LoadType.REFRESH) {
                                productDao.getProductById(it.product.productId).first()
                            } else {
                                oldData.firstOrNull { old -> old.product.productId == it.product.productId }
                            }

                        currentData?.let { current ->
                            it.product.isFavorite = current.product.isFavorite
                        }

                        productDao.insert(it.product)
                        productImageDao.insertAll(it.images)
                    }
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ProductWithImages>
    ): ProductRemoteKeysEntity? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.product?.productId?.let {
                productRemoteKeysDao.getProductRemoteKeys(productId = it)
            }
        }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, ProductWithImages>
    ): ProductRemoteKeysEntity? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            productRemoteKeysDao.getProductRemoteKeys(productId = it.product.productId)
        }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, ProductWithImages>
    ): ProductRemoteKeysEntity? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            productRemoteKeysDao.getProductRemoteKeys(productId = it.product.productId)
        }
}
