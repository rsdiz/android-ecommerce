package id.rsdiz.rdshop.domain.usecase.category

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.Category
import id.rsdiz.rdshop.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [ICategoryUseCase]
 */
class CategoryUseCase @Inject constructor(
    private val repository: ICategoryRepository
) : ICategoryUseCase {
    override fun getCategories(): Flow<Resource<List<Category>>> = repository.getCategories()

    override fun getCategory(categoryId: String): Flow<Resource<Category>> =
        repository.getCategory(categoryId)

    override suspend fun insertCategory(category: Category) = repository.insertCategory(category)

    override suspend fun updateCategory(categoryId: String, category: Category) =
        repository.updateCategory(categoryId, category)

    override suspend fun deleteCategory(categoryId: String) = repository.deleteCategory(categoryId)
}
