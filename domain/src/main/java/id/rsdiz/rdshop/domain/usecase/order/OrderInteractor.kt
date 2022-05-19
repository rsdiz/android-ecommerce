package id.rsdiz.rdshop.domain.usecase.order

import androidx.paging.PagingData
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.domain.repository.order.IOrderRepository
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

/**
 * Implementation of [OrderUseCase]
 */
class OrderInteractor @Inject constructor(
    private val repository: IOrderRepository
) : OrderUseCase {
    override suspend fun count() = repository.count()

    override fun getOrders(status: Short?): Flow<PagingData<Order>> = repository.getOrders(status = status)

    override fun getNewestOrders(): Flow<Resource<List<Order>>> = repository.getNewestOrders()

    override fun getOrder(orderId: String): Flow<Resource<Order>> = repository.getOrder(orderId)

    override fun getOrderByUserId(userId: String): Flow<Resource<List<Order>>> =
        repository.getOrderByUserId(userId)

    override fun getOrderByDate(
        startDate: OffsetDateTime?,
        endDate: OffsetDateTime?
    ): Flow<Resource<List<Order>>> = repository.getOrderByDate(startDate, endDate)

    override suspend fun insertOrder(order: Order): Resource<String> = repository.insertOrder(order)

    override suspend fun updateOrder(
        orderId: String,
        status: Short,
        trackingNumber: String
    ): Resource<String> =
        repository.updateOrder(orderId, status, trackingNumber)
}
