package id.rsdiz.rdshop.domain.usecase.category

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Contract for Category Use Case
 */
interface ICategoryUseCase {

    /**
     * Count total row in categories
     */
    suspend fun count(): Resource<Int>

    /**
     * Get list of category
     */
    fun getCategories(): Flow<Resource<List<Category>>>

    /**
     * Insert new category
     */
    suspend fun insertCategory(name: String): Resource<String>

    /**
     * Update category by [categoryId]
     */
    suspend fun updateCategory(categoryId: String, newName: String): Resource<String>

    /**
     * Delete category
     */
    suspend fun deleteCategory(categoryId: String): Resource<String>
}
