package id.rsdiz.rdshop.domain.repository

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Contract for Product Repository
 */
interface IProductRepository {

    /**
     * Get list of products
     */
    fun getProducts(): Flow<Resource<List<Product>>>

    /**
     * Get specified product by [productId]
     */
    fun getProduct(productId: String): Flow<Resource<Product>>

    /**
     * Search product in repository
     */
    suspend fun searchProduct(query: String): Resource<List<Product>>

    /**
     * Insert new product to repository
     */
    suspend fun insertProduct(product: Product)

    /**
     * Update product by [productId]
     */
    suspend fun updateProduct(productId: String, product: Product)

    /**
     * Delete product from repository
     */
    suspend fun deleteProduct(productId: String)
}
