package id.rsdiz.rdshop.ui.home.ui.profile.order

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.common.OrderItemUIState
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.usecase.order.OrderInteractor
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderInteractor: OrderInteractor
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()

    private var _orderData = MutableLiveData<List<OrderItemUIState>>()
    val orderData = _orderData as LiveData<List<OrderItemUIState>>

    private fun getOrderByUserId(userId: String) = reloadTrigger.switchMap {
        orderInteractor.getOrderByUserId(userId = userId).asLiveData(viewModelScope.coroutineContext)
    }

    fun observerOrder(owner: LifecycleOwner, userId: String) {
        getOrderByUserId(userId).observe(owner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { list ->
                        val data = list.map {
                            OrderItemUIState(it)
                        }
                        _orderData.postValue(data)
                    }
                }
                is Resource.Error -> {
                    Log.e(
                        "RDSHOP-ERROR",
                        "observerOrder: Error Occurred, cause: ${response.message}"
                    )
                }
                else -> {}
            }
        }
    }

    fun refresh() {
        reloadTrigger.value = true
    }

    init {
        refresh()
    }
}
