package id.rsdiz.rdshop.domain.usecase.product

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.Product
import id.rsdiz.rdshop.domain.repository.IProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [IProductUseCase]
 */
class ProductUseCase @Inject constructor(
    private val repository: IProductRepository
) : IProductUseCase {
    override fun getProducts(): Flow<Resource<List<Product>>> = repository.getProducts()

    override fun getProduct(productId: String): Flow<Resource<Product>> = repository.getProduct(productId)

    override suspend fun searchProduct(query: String): Resource<List<Product>> = repository.searchProduct(query)

    override suspend fun insertProduct(product: Product) = repository.insertProduct(product)

    override suspend fun updateProduct(productId: String, product: Product) = repository.updateProduct(productId, product)

    override suspend fun deleteProduct(productId: String) = repository.deleteProduct(productId)
}
