package id.rsdiz.rdshop.ui.home.ui.cart.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.domain.usecase.ongkir.OngkirInteractor
import id.rsdiz.rdshop.domain.usecase.order.OrderInteractor
import id.rsdiz.rdshop.domain.usecase.product.ProductInteractor
import id.rsdiz.rdshop.domain.usecase.user.UserInteractor
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val productInteractor: ProductInteractor,
    private val userInteractor: UserInteractor,
    private val ongkirInteractor: OngkirInteractor,
    private val orderInteractor: OrderInteractor
) : ViewModel() {
    fun getProduct(productId: String) =
        productInteractor.getProduct(productId).asLiveData(viewModelScope.coroutineContext)

    fun getUser(userId: String) =
        userInteractor.getUser(userId).asLiveData(viewModelScope.coroutineContext).asFlow()

    suspend fun getCities() = ongkirInteractor.getCities()

    suspend fun getProvinces() = ongkirInteractor.getProvinces()

    suspend fun getShippingCost(origin: Int, destination: Int, weight: Int, courier: String) =
        ongkirInteractor.getShippingCost(origin, destination, weight, courier)

    suspend fun createOrder(orderRequest: Order) = orderInteractor.insertOrder(order = orderRequest)
}
