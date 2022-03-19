package id.rsdiz.rdshop.data.source.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import id.rsdiz.rdshop.data.source.local.entity.ProductEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductWithImages
import id.rsdiz.rdshop.data.source.local.room.base.IBaseDao
import id.rsdiz.rdshop.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Contracts how application interacts with stored data in [ProductEntity]
 */
@Dao
interface IProductDao : IBaseDao<ProductEntity> {
    @Transaction
    @Query("SELECT * FROM products")
    fun getAllProducts(): PagingSource<Int, Product>

    @Transaction
    @Query("SELECT * FROM products WHERE productId = :productId")
    fun getProductById(productId: String): Flow<ProductWithImages>

    @Transaction
    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductByCategoryId(categoryId: String): Flow<List<ProductWithImages>>

    @Transaction
    @Query("SELECT * FROM products WHERE name LIKE :word OR description LIKE :word")
    fun searchProducts(word: String): Flow<List<ProductWithImages>>

    @Query("SELECT * FROM products WHERE name LIKE :word AND price BETWEEN :minPrice AND :maxPrice ORDER BY price")
    fun getProductByFilter(
        word: String? = "",
        minPrice: Int? = 0,
        maxPrice: Int? = Int.MAX_VALUE
    ): Flow<List<ProductWithImages>>

    @Query("DELETE FROM products")
    fun deleteAll()
}
