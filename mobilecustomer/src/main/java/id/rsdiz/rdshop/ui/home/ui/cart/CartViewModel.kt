package id.rsdiz.rdshop.ui.home.ui.cart

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.domain.usecase.category.CategoryInteractor
import id.rsdiz.rdshop.domain.usecase.product.ProductInteractor
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val productInteractor: ProductInteractor,
    private val categoryInteractor: CategoryInteractor
) : ViewModel() {
    private val _categoryData = MutableLiveData<List<Category>>()
    val categoryData = _categoryData as LiveData<List<Category>>
    private val _productData = MutableLiveData<List<Product>>()
    val productData = _productData as LiveData<List<Product>>

    private fun getProduct(productId: String) =
        productInteractor.getProduct(productId).asLiveData(viewModelScope.coroutineContext)

    private fun getCategory() =
        categoryInteractor.getCategories().asLiveData(viewModelScope.coroutineContext)

    suspend fun countCategory() = categoryInteractor.count()

    fun observerProduct(owner: LifecycleOwner, productId: String) {
        getProduct(productId).observe(owner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        val newData = mutableListOf<Product>()
                        _productData.value?.let { old -> newData.addAll(old) }
                        if (!newData.contains(it))
                            newData.add(it)
                        _productData.postValue(newData)
                    }
                }
                is Resource.Error -> {
                    Log.e(
                        "RDSHOP-ERROR",
                        "observerProduct: Error Occurred, cause: ${response.message}"
                    )
                }
                else -> {}
            }
        }
    }

    fun productHasObserver(productId: String) = getProduct(productId).hasActiveObservers()

    fun observeCategory(owner: LifecycleOwner) {
        getCategory().observe(owner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        _categoryData.postValue(it)
                    }
                }
                is Resource.Error -> {
                    Log.e(
                        "RDSHOP-ERROR",
                        "observeCategory: Error Occurred, cause: ${response.message}"
                    )
                }
                else -> {}
            }
        }
    }
}
