package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Query
import id.rsdiz.rdshop.data.source.local.entity.CategoryEntity
import id.rsdiz.rdshop.data.source.local.room.base.IBaseDao
import kotlinx.coroutines.flow.Flow

/**
 * Contracts how application interacts with stored data in [CategoryEntity]
 */
interface ICategoryDao : IBaseDao<CategoryEntity> {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE name LIKE :word")
    fun searchCategories(word: String): Flow<List<CategoryEntity>>
}
