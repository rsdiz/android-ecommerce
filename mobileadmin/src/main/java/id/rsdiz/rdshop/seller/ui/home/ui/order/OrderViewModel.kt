package id.rsdiz.rdshop.seller.ui.home.ui.order

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.domain.usecase.order.OrderInteractor
import id.rsdiz.rdshop.seller.common.OrderItemUIState
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderInteractor: OrderInteractor
) : ViewModel() {
    fun getOrders(status: Short?) =
        orderInteractor.getOrders(status)
            .map { pagingData ->
                pagingData.map { order -> OrderItemUIState(order) }
            }.cachedIn(viewModelScope)
}
