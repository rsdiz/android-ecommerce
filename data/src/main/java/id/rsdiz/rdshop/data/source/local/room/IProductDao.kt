package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Query
import androidx.room.Transaction
import id.rsdiz.rdshop.data.source.local.entity.ProductEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductWithImages
import id.rsdiz.rdshop.data.source.local.room.base.IBaseDao
import kotlinx.coroutines.flow.Flow

/**
 * Contracts how application interacts with stored data in [ProductEntity]
 */
interface IProductDao : IBaseDao<ProductEntity> {
    @Transaction
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<ProductWithImages>

    @Transaction
    @Query("SELECT * FROM products WHERE productId = :productId")
    fun getProductById(productId: String): Flow<ProductWithImages>

    @Transaction
    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductByCategoryId(categoryId: String): Flow<ProductWithImages>

    @Transaction
    @Query("SELECT * FROM products WHERE name LIKE :word OR description LIKE :word")
    fun searchProducts(word: String): Flow<ProductWithImages>

    @Query("SELECT * FROM products WHERE name LIKE :word AND price BETWEEN :minPrice AND :maxPrice ORDER BY price")
    fun getProductByFilter(
        word: String? = "",
        minPrice: Int? = 0,
        maxPrice: Int? = Int.MAX_VALUE
    ): Flow<ProductWithImages>
}
