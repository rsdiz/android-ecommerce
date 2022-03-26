package id.rsdiz.rdshop.seller.ui.home.ui.dashboard

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.domain.usecase.auth.AuthUseCase
import id.rsdiz.rdshop.domain.usecase.category.CategoryUseCase
import id.rsdiz.rdshop.domain.usecase.order.OrderUseCase
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import id.rsdiz.rdshop.domain.usecase.user.UserUseCase
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val orderUseCase: OrderUseCase,
    private val userUseCase: UserUseCase,
    private val productUseCase: ProductUseCase,
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()
    private var _newestOrder = orderUseCase.getNewestOrders()

    suspend fun countData() = listOf(
        userUseCase.count(),
        categoryUseCase.count(),
        productUseCase.count(),
        orderUseCase.count()
    )

    init {
        refreshOrder()
    }

    val newestOrder: LiveData<Resource<List<Order>>>
        get() = reloadTrigger.switchMap {
            _newestOrder.asLiveData(viewModelScope.coroutineContext)
        }

    fun refreshOrder() {
        reloadTrigger.value = true
    }

    suspend fun signOut(token: String) = authUseCase.signOut(token)
}
