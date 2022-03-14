package id.rsdiz.rdshop.domain.usecase.order

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.Order
import id.rsdiz.rdshop.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [IOrderUseCase]
 */
class OrderUseCase @Inject constructor(
    private val repository: IOrderRepository
) : IOrderUseCase {
    override fun getOrders(): Flow<Resource<List<Order>>> = repository.getOrders()

    override fun getOrder(orderId: String): Flow<Resource<Order>> = repository.getOrder(orderId)

    override suspend fun searchOrder(query: String): Resource<List<Order>> =
        repository.searchOrder(query)

    override suspend fun insertOrder(order: Order) = repository.insertOrder(order)

    override suspend fun updateOrder(orderId: String, order: Order) =
        repository.updateOrder(orderId, order)
}
