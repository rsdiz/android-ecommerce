package id.rsdiz.rdshop.ui.home.ui.home

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.common.ProductItemUiState
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.domain.usecase.category.CategoryUseCase
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()
    private var _categories = categoryUseCase.getCategories()

    val categories: LiveData<Resource<List<Category>>>
        get() = reloadTrigger.switchMap {
            _categories.asLiveData(viewModelScope.coroutineContext)
        }

    fun refreshCategory() {
        reloadTrigger.value = true
    }

    fun getProducts() =
        productUseCase.getProducts()
            .map { pagingData ->
                pagingData.map { ProductItemUiState(it) }
            }.cachedIn(viewModelScope)

    suspend fun searchProducts(query: String) = productUseCase.searchProduct(
        query = StringBuilder("%").append(query).append("%").toString()
    )

    init {
        refreshCategory()
    }
}
