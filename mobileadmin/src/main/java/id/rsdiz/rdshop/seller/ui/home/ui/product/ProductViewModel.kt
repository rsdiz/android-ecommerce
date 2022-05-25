package id.rsdiz.rdshop.seller.ui.home.ui.product

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Category
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
    categoryInteractor: CategoryInteractor
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()
    private val _categories = categoryInteractor.getCategories()

    val categories: LiveData<Resource<List<Category>>>
        get() = _categories.asLiveData(viewModelScope.coroutineContext)

    fun getProducts() =
        productInteractor.getProducts()
            .map { pagingData ->
                pagingData.map { ProductItemUiState(it) }
            }.cachedIn(viewModelScope)

    fun getProductById(productId: String) = reloadTrigger.switchMap {
        productInteractor.getProduct(productId).asLiveData(viewModelScope.coroutineContext)
    }

    suspend fun getProductCount(type: String?) = productInteractor.count(type)

    suspend fun updateProducts(product: Product, imageFile: File?) =
        productInteractor.updateProduct(
            product = product,
            sourceFile = imageFile
        )

    suspend fun addImageCatalog(productId: String, imageFile: File) =
        productInteractor.addProductImage(productId, imageFile)

    suspend fun removeImageCatalog(productId: String, imageId: String) =
        productInteractor.removeProductImage(productId, imageId)

    suspend fun createProduct(product: Product, imageFile: File?) = productInteractor.insertProduct(
        product = product,
        sourceFile = imageFile
    )

    suspend fun deleteProduct(productId: String) = productInteractor.deleteProduct(productId)

    fun refreshProduct() {
        reloadTrigger.value = true
    }
}
