package id.rsdiz.rdshop.ui.home.ui.detail

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.category.CategoryUseCase
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()
    private var _categories = categoryUseCase.getCategories()

    fun getProduct(productId: String) = reloadTrigger.switchMap {
        productUseCase.getProduct(productId).asLiveData(viewModelScope.coroutineContext)
    }.asFlow()

    fun getCategories() =
        _categories.asLiveData(viewModelScope.coroutineContext)

    fun refreshCategory() {
        reloadTrigger.value = true
    }

    suspend fun switchFavorite(productId: String) = productUseCase.switchProductFavorite(productId)

    init {
        refreshCategory()
    }
}