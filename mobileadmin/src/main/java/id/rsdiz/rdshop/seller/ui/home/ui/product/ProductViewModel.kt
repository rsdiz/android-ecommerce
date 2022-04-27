package id.rsdiz.rdshop.seller.ui.home.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.domain.usecase.category.CategoryUseCase
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import id.rsdiz.rdshop.seller.common.ProductItemUiState
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    fun getProducts() =
        productUseCase.getProducts()
            .map { pagingData ->
                pagingData.map { ProductItemUiState(it) }
            }.cachedIn(viewModelScope)

    suspend fun getProductCount() = productUseCase.count()

    fun getCategories() =
        categoryUseCase.getCategories().asLiveData(viewModelScope.coroutineContext)

    suspend fun updateProducts(product: Product, imageFile: File?) = productUseCase.updateProduct(
        product = product,
        sourceFile = imageFile
    )

    suspend fun createProduct(product: Product, imageFile: File?) = productUseCase.insertProduct(
        product = product,
        sourceFile = imageFile
    )
}
