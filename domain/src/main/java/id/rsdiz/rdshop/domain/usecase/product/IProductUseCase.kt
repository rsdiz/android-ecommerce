package id.rsdiz.rdshop.domain.usecase.product

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Contract for Product Use Case
 */
interface IProductUseCase {

    /**
     * Get list of products
     */
    fun getProducts(): Flow<Resource<List<Product>>>

    /**
     * Get specified product by [productId]
     */
    fun getProduct(productId: String): Flow<Resource<Product>>

    /**
     * Search product
     */
    suspend fun searchProduct(query: String): Resource<List<Product>>

    /**
     * Insert new product
     */
    suspend fun insertProduct(product: Product)

    /**
     * Update product by [productId]
     */
    suspend fun updateProduct(productId: String, product: Product)

    /**
     * Delete product
     */
    suspend fun deleteProduct(productId: String)
}
