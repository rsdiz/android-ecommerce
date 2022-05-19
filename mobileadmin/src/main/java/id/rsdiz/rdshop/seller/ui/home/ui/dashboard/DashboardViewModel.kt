package id.rsdiz.rdshop.seller.ui.home.ui.dashboard

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.domain.usecase.auth.AuthInteractor
import id.rsdiz.rdshop.domain.usecase.category.CategoryInteractor
import id.rsdiz.rdshop.domain.usecase.order.OrderInteractor
import id.rsdiz.rdshop.domain.usecase.product.ProductInteractor
import id.rsdiz.rdshop.domain.usecase.user.UserInteractor
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val orderInteractor: OrderInteractor,
    private val userInteractor: UserInteractor,
    private val productInteractor: ProductInteractor,
    private val categoryInteractor: CategoryInteractor
) : ViewModel() {
    private var reloadTrigger = MutableLiveData<Boolean>()
    private var _newestOrder = orderInteractor.getNewestOrders()

    suspend fun countUser() = userInteractor.count()
    suspend fun countCategory() = categoryInteractor.count()
    suspend fun countProduct() = productInteractor.count()
    suspend fun countOrder() = orderInteractor.count()

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

    suspend fun signOut(token: String) = authInteractor.signOut(token)
}
