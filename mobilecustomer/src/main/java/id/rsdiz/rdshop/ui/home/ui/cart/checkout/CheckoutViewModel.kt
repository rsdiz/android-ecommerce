package id.rsdiz.rdshop.ui.home.ui.cart.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.ongkir.OngkirUseCase
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import id.rsdiz.rdshop.domain.usecase.user.UserUseCase
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val productUseCase: ProductUseCase,
    private val userUseCase: UserUseCase,
    private val ongkirUseCase: OngkirUseCase
) : ViewModel() {
    fun getProduct(productId: String) =
        productUseCase.getProduct(productId).asLiveData(viewModelScope.coroutineContext)

    fun getUser(userId: String) =
        userUseCase.getUser(userId).asLiveData(viewModelScope.coroutineContext).asFlow()

    suspend fun getCities() = ongkirUseCase.getCities()

    suspend fun getProvinces() = ongkirUseCase.getProvinces()

    suspend fun getShippingCost(origin: Int, destination: Int, weight: Int, courier: String) =
        ongkirUseCase.getShippingCost(origin, destination, weight, courier)
}
