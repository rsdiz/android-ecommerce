package id.rsdiz.rdshop.seller.ui.home.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import id.rsdiz.rdshop.seller.common.ProductItemUiState
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productUseCase: ProductUseCase
) : ViewModel() {
    fun getProducts() =
        productUseCase.getProducts()
            .map { pagingData ->
                pagingData.map { ProductItemUiState(it) }
            }.cachedIn(viewModelScope)

    suspend fun getProductCount() = productUseCase.count()
}
