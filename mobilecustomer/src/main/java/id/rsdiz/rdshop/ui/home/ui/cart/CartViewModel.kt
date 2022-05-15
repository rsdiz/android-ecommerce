package id.rsdiz.rdshop.ui.home.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.category.CategoryUseCase
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    fun getProduct(productId: String) =
        productUseCase.getProduct(productId).asLiveData(viewModelScope.coroutineContext)

    fun getCategory() =
        categoryUseCase.getCategories().asLiveData(viewModelScope.coroutineContext)

    suspend fun countCategory() = categoryUseCase.count()
}
