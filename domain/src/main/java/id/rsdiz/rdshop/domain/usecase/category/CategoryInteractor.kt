package id.rsdiz.rdshop.domain.usecase.category

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.domain.repository.category.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [CategoryUseCase]
 */
class CategoryInteractor @Inject constructor(
    private val repository: ICategoryRepository
) : CategoryUseCase {
    override suspend fun count() = repository.count()

    override fun getCategories(): Flow<Resource<List<Category>>> = repository.getCategories()

    override suspend fun insertCategory(name: String): Resource<String> =
        repository.insertCategory(name)

    override suspend fun updateCategory(categoryId: String, newName: String): Resource<String> =
        repository.updateCategory(categoryId, newName)

    override suspend fun deleteCategory(categoryId: String) = repository.deleteCategory(categoryId)
}
