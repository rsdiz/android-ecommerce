package id.rsdiz.rdshop.seller.ui.home.ui.detailorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.order.OrderInteractor
import id.rsdiz.rdshop.domain.usecase.product.ProductInteractor
import id.rsdiz.rdshop.domain.usecase.user.UserInteractor
import javax.inject.Inject

@HiltViewModel
class DetailOrderViewModel @Inject constructor(
    private val orderInteractor: OrderInteractor,
    private val productInteractor: ProductInteractor,
    private val userInteractor: UserInteractor
) : ViewModel() {
    fun getOrder(orderId: String) = orderInteractor.getOrder(orderId)
    fun getProduct(productId: String) =
        productInteractor.getProduct(productId).asLiveData(viewModelScope.coroutineContext)

    fun getUser(userId: String) = userInteractor.getUser(userId)
    suspend fun updateOrderStatus(orderId: String, status: Short, trackingNumber: String = "") =
        orderInteractor.updateOrder(
            orderId = orderId,
            status = status,
            trackingNumber = trackingNumber
        )
}
