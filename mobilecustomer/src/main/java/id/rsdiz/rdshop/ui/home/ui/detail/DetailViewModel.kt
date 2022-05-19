package id.rsdiz.rdshop.ui.home.ui.detail

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.category.CategoryInteractor
import id.rsdiz.rdshop.domain.usecase.product.ProductInteractor
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productInteractor: ProductInteractor,
    private val categoryInteractor: CategoryInteractor
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()

    fun getProduct(productId: String) = reloadTrigger.switchMap {
        productInteractor.getProduct(productId).asLiveData(viewModelScope.coroutineContext)
    }.asFlow()

    fun getCategories() = categoryInteractor.getCategories()

    suspend fun countCategories() = categoryInteractor.count()

    fun refresh() {
        reloadTrigger.value = true
    }

    suspend fun switchFavorite(productId: String) = productInteractor.switchProductFavorite(productId)

    init {
        refresh()
    }
}
