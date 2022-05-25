package id.rsdiz.rdshop.ui.auth.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.common.ProductItemUiState
import id.rsdiz.rdshop.domain.usecase.product.ProductInteractor
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val productInteractor: ProductInteractor
) :
    ViewModel() {
    fun getProducts() =
        productInteractor.getProducts()
            .map { pagingData ->
                pagingData.map { ProductItemUiState(it) }
            }.cachedIn(viewModelScope)

    suspend fun getProductCount(type: String) = productInteractor.count(type)
}
