package id.rsdiz.rdshop.seller.ui.home.ui.detailorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.order.OrderUseCase
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import id.rsdiz.rdshop.domain.usecase.user.UserUseCase
import javax.inject.Inject

@HiltViewModel
class DetailOrderViewModel @Inject constructor(
    private val orderUseCase: OrderUseCase,
    private val productUseCase: ProductUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    fun getOrder(orderId: String) = orderUseCase.getOrder(orderId)
    fun getProduct(productId: String) =
        productUseCase.getProduct(productId).asLiveData(viewModelScope.coroutineContext)

    fun getUser(userId: String) = userUseCase.getUser(userId)
    suspend fun updateOrderStatus(orderId: String, status: Short, trackingNumber: String = "") =
        orderUseCase.updateOrder(
            orderId = orderId,
            status = status,
            trackingNumber = trackingNumber
        )
}
