package id.rsdiz.rdshop.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.rsdiz.rdshop.base.utils.AppExecutors
import id.rsdiz.rdshop.data.NetworkBoundResource
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.paging.ProductRemoteMediator
import id.rsdiz.rdshop.data.source.local.ProductLocalDataSource
import id.rsdiz.rdshop.data.source.remote.ProductRemoteDataSource
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import id.rsdiz.rdshop.data.source.remote.response.product.ProductResponse
import id.rsdiz.rdshop.domain.model.Product
import id.rsdiz.rdshop.domain.model.ProductImage
import id.rsdiz.rdshop.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IProductRepository] from Domain Layer
 */
@Singleton
class ProductRepository @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource,
    private val appExecutors: AppExecutors
) : IProductRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getProducts(): Flow<PagingData<Product>> = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = ProductRemoteMediator(
            apiService = remoteDataSource.apiService,
            productDao = localDataSource.productDao,
            productImageDao = localDataSource.productImageDao,
            productRemoteKeysDao = localDataSource.productRemoteKeysDao,
            mapper = remoteDataSource.mapper
        ),
        pagingSourceFactory = { localDataSource.getAllProducts() }
    ).flow

    override fun getProduct(productId: String): Flow<Resource<Product>> =
        object : NetworkBoundResource<Product, ProductResponse>() {
            override fun loadFromDB(): Flow<Product?> =
                localDataSource.getProductById(productId = productId).map {
                    localDataSource.mapper.mapFromEntity(it)
                }

            override fun shouldFetch(data: Product?): Boolean = data?.productId.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<ProductResponse>> =
                remoteDataSource.getProductById(productId = productId)

            override suspend fun saveCallResult(data: ProductResponse) =
                remoteDataSource.mapper.mapRemoteToEntity(data).let {
                    localDataSource.insert(it)
                }
        }.asFlow() as Flow<Resource<Product>>

    override suspend fun searchProduct(query: String): Resource<List<Product>> {
        val result = localDataSource.searchProducts(query).first()
        val data = localDataSource.mapper.mapFromEntities(result)

        return Resource.Success(data)
    }

    override suspend fun insertProduct(product: Product, sourceFile: File?): Resource<String> =
        when (
            val response = remoteDataSource.createProduct(
                product = product,
                sourceFile = sourceFile
            ).first()
        ) {
            is ApiResponse.Success -> {
                remoteDataSource.mapper.mapRemoteToEntity(response.data!!).let {
                    localDataSource.insert(it)
                }

                Resource.Success("User Successfully Added!")
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun updateProduct(product: Product, sourceFile: File?): Resource<String> =
        when (val response = remoteDataSource.updateProduct(product, sourceFile).first()) {
            is ApiResponse.Success -> {
                localDataSource.mapper.mapToEntity(product).let {
                    appExecutors.diskIO().execute {
                        localDataSource.update(it.product)
                    }
                }

                Resource.Success("User Successfully Updated!")
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun deleteProduct(productId: String): Resource<String> =
        when (val response = remoteDataSource.deleteProduct(productId = productId).first()) {
            is ApiResponse.Success -> {
                val data = localDataSource.getProductById(productId).first()
                data.let { localDataSource.delete(it) }

                Resource.Success(response.data!!)
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun addProductImage(
        productId: String,
        sourceFile: File
    ): Resource<ProductImage> =
        when (
            val response = remoteDataSource.addImage(
                productId = productId,
                sourceFile = sourceFile
            ).first()
        ) {
            is ApiResponse.Success -> {
                remoteDataSource.mapper.mapProductImageRemoteToProductImageEntity(response.data!!)
                    .let {
                        localDataSource.productImageDao.insert(it)

                        Resource.Success(
                            localDataSource.mapper.mapProductImageEntityToProductImage(
                                it
                            )
                        )
                    }
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun removeProductImage(productId: String, imageId: String): Resource<String> =
        when (
            val response = remoteDataSource.deleteImage(
                productId = productId,
                imageId = imageId
            ).first()
        ) {
            is ApiResponse.Success -> {
                val data = localDataSource.getProductById(productId).first()
                data.let { localDataSource.delete(it) }

                Resource.Success(response.data!!)
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }
}
