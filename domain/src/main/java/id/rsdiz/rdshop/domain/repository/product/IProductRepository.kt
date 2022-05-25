package id.rsdiz.rdshop.domain.repository.product

import androidx.paging.PagingData
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.data.model.ProductImage
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Contract for Product Repository
 */
interface IProductRepository {

    /**
     * Count total row in products
     */
    suspend fun count(type: String?): Resource<Int>

    /**
     * Get list of products
     */
    fun getProducts(): Flow<PagingData<Product>>

    /**
     * Get List of Favorite Products
     */
    fun getFavoriteProducts(): Flow<List<Product>>

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
    suspend fun insertProduct(product: Product, sourceFile: File?): Resource<String>

    /**
     * Update product
     */
    suspend fun updateProduct(product: Product, sourceFile: File?): Resource<String>

    /**
     * Delete product from repository
     */
    suspend fun deleteProduct(productId: String): Resource<String>

    /**
     * Insert Product Image to repository
     */
    suspend fun addProductImage(productId: String, sourceFile: File): Resource<ProductImage>

    /**
     * Delete Product Image from repository
     */
    suspend fun removeProductImage(productId: String, imageId: String): Resource<String>

    /**
     * Add Product to Favorite Product
     */
    suspend fun switchProductFavorite(productId: String)
}
