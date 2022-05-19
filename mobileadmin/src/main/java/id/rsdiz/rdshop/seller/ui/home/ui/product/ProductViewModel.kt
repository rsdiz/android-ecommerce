package id.rsdiz.rdshop.seller.ui.home.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.domain.usecase.category.CategoryInteractor
import id.rsdiz.rdshop.domain.usecase.product.ProductInteractor
import id.rsdiz.rdshop.seller.common.ProductItemUiState
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productInteractor: ProductInteractor,
    private val categoryInteractor: CategoryInteractor
) : ViewModel() {
    fun getProducts() =
        productInteractor.getProducts()
            .map { pagingData ->
                pagingData.map { ProductItemUiState(it) }
            }.cachedIn(viewModelScope)

    suspend fun getProductCount() = productInteractor.count()

    fun getCategories() =
        categoryInteractor.getCategories().asLiveData(viewModelScope.coroutineContext)

    suspend fun updateProducts(product: Product, imageFile: File?) = productInteractor.updateProduct(
        product = product,
        sourceFile = imageFile
    )

    suspend fun createProduct(product: Product, imageFile: File?) = productInteractor.insertProduct(
        product = product,
        sourceFile = imageFile
    )
}
