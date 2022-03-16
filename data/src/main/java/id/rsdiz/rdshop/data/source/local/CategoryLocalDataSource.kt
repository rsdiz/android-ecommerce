package id.rsdiz.rdshop.data.source.local

import id.rsdiz.rdshop.data.source.local.entity.CategoryEntity
import id.rsdiz.rdshop.data.source.local.mapper.CategoryMapper
import id.rsdiz.rdshop.data.source.local.room.ICategoryDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryLocalDataSource @Inject constructor(
    private val categoryDao: ICategoryDao,
    val mapper: CategoryMapper
) {
    fun getAllCategories() = categoryDao.getAllCategories()

    fun searchCategories(word: String) = categoryDao.searchCategories(word)

    fun update(data: CategoryEntity) = categoryDao.update(data)

    suspend fun insertAll(list: List<CategoryEntity>) = categoryDao.insertAll(list)

    suspend fun insert(data: CategoryEntity) = categoryDao.insert(data)

    suspend fun delete(data: CategoryEntity) = categoryDao.delete(data)
}
