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
     * Insert new category to repository
     */
    suspend fun insertCategory(name: String): Resource<String>

    /**
     * Update category by [categoryId]
     */
    suspend fun updateCategory(categoryId: String, newName: String): Resource<String>

    /**
     * Delete category from repository
     */
    suspend fun deleteCategory(categoryId: String): Resource<String>
}
