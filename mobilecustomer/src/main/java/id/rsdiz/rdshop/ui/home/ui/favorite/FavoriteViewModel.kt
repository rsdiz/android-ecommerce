package id.rsdiz.rdshop.ui.home.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.common.ProductItemUiState
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val productUseCase: ProductUseCase
) : ViewModel() {
    fun getProducts(): Flow<PagingData<ProductItemUiState>> =
        productUseCase.getProducts()
            .map { pagingData ->
                pagingData.map { product ->
                    ProductItemUiState(product)
                }
            }.cachedIn(viewModelScope)
}
