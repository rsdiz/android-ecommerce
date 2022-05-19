package id.rsdiz.rdshop.seller.ui.home.ui.category

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.domain.usecase.category.CategoryInteractor
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryInteractor: CategoryInteractor
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()
    private var _categories = categoryInteractor.getCategories()

    init {
        refreshCategory()
    }

    fun refreshCategory() {
        reloadTrigger.value = true
    }

    suspend fun addCategory(name: String) = categoryInteractor.insertCategory(name = name)

    suspend fun deleteCategory(categoryId: String) =
        categoryInteractor.deleteCategory(categoryId = categoryId)

    suspend fun updateCategory(category: Category) =
        categoryInteractor.updateCategory(categoryId = category.categoryId, newName = category.name)

    val categories: LiveData<Resource<List<Category>>>
        get() = reloadTrigger.switchMap {
            _categories.asLiveData(viewModelScope.coroutineContext)
        }
}
