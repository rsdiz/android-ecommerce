package id.rsdiz.rdshop.domain.repository.category

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Contract for Category Repository
 */
interface ICategoryRepository {

    /**
     * Get list of category
     */
    fun getCategories(): Flow<Resource<List<Category>>>

    /**
     * Get specified category by [categoryId]
     */
    fun getCategory(categoryId: String): Flow<Resource<Category>>

    /**
     * Insert new category to repository
     */
    suspend fun insertCategory(category: Category)

    /**
     * Update category by [categoryId]
     */
    suspend fun updateCategory(categoryId: String, category: Category)

    /**
     * Delete category from repository
     */
    suspend fun deleteCategory(categoryId: String)
}
