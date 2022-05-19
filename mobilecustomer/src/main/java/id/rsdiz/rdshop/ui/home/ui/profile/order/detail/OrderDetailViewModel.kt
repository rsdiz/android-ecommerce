package id.rsdiz.rdshop.ui.home.ui.profile.order.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.order.OrderInteractor
import id.rsdiz.rdshop.domain.usecase.product.ProductInteractor
import id.rsdiz.rdshop.domain.usecase.user.UserInteractor
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderInteractor: OrderInteractor,
    private val productInteractor: ProductInteractor,
    private val userInteractor: UserInteractor
) : ViewModel() {
    fun getOrder(orderId: String) = orderInteractor.getOrder(orderId)
    fun getProduct(productId: String) =
        productInteractor.getProduct(productId).asLiveData(viewModelScope.coroutineContext)
}
