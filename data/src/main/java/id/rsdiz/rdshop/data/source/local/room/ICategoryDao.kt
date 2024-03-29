package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Dao
import androidx.room.Query
import id.rsdiz.rdshop.data.source.local.entity.CategoryEntity
import id.rsdiz.rdshop.data.source.local.room.base.IBaseDao
import kotlinx.coroutines.flow.Flow

/**
 * Contracts how application interacts with stored data in [CategoryEntity]
 */
@Dao
interface ICategoryDao : IBaseDao<CategoryEntity> {
    @Query("SELECT COUNT(categoryId) FROM categories")
    fun count(): Int

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    fun getCategoryById(categoryId: String): Flow<CategoryEntity>

    @Query("SELECT * FROM categories WHERE name LIKE :word")
    fun searchCategories(word: String): Flow<List<CategoryEntity>>
}
