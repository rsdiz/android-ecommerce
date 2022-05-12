package id.rsdiz.rdshop.domain.usecase.product

import androidx.paging.PagingData
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.domain.repository.product.IProductRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

/**
 * Implementation of [IProductUseCase]
 */
class ProductUseCase @Inject constructor(
    private val repository: IProductRepository
) : IProductUseCase {
    override suspend fun count() = repository.count()

    override fun getProducts(): Flow<PagingData<Product>> = repository.getProducts()

    override fun getProduct(productId: String): Flow<Resource<Product>> =
        repository.getProduct(productId)

    override suspend fun searchProduct(query: String): Resource<List<Product>> =
        repository.searchProduct(query)

    override suspend fun insertProduct(product: Product, sourceFile: File?): Resource<String> =
        repository.insertProduct(product, sourceFile)

    override suspend fun updateProduct(product: Product, sourceFile: File?): Resource<String> =
        repository.updateProduct(product, sourceFile)

    override suspend fun deleteProduct(productId: String): Resource<String> =
        repository.deleteProduct(productId)

    override suspend fun addProductImage(
        productId: String,
        sourceFile: File
    ) = repository.addProductImage(productId, sourceFile)

    override suspend fun removeProductImage(productId: String, imageId: String) =
        repository.removeProductImage(productId, imageId)

    override suspend fun switchProductFavorite(productId: String) =
        repository.switchProductFavorite(productId)
}
