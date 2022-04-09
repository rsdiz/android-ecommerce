package id.rsdiz.rdshop.seller.ui.home.ui.category

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.domain.usecase.category.CategoryUseCase
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()
    private var _categories = categoryUseCase.getCategories()

    init {
        refreshCategory()
    }

    fun refreshCategory() {
        reloadTrigger.value = true
    }

    suspend fun addCategory(name: String) = categoryUseCase.insertCategory(name = name)

    val categories: LiveData<Resource<List<Category>>>
        get() = reloadTrigger.switchMap {
            _categories.asLiveData(viewModelScope.coroutineContext)
        }
}
