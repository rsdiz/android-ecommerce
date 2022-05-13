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

    fun getProduct(productId: String) = reloadTrigger.switchMap {
        productUseCase.getProduct(productId).asLiveData(viewModelScope.coroutineContext)
    }.asFlow()

    fun getCategories() = categoryUseCase.getCategories()

    suspend fun countCategories() = categoryUseCase.count()

    fun refresh() {
        reloadTrigger.value = true
    }

    suspend fun switchFavorite(productId: String) = productUseCase.switchProductFavorite(productId)

    init {
        refresh()
    }
}
