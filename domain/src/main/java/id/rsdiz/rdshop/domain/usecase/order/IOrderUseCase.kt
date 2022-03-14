package id.rsdiz.rdshop.domain.usecase.order

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.Order
import kotlinx.coroutines.flow.Flow

/**
 * Contract for Order Use Case
 */
interface IOrderUseCase {

    /**
     * Get list of orders
     */
    fun getOrders(): Flow<Resource<List<Order>>>

    /**
     * Get specified order by [orderId]
     */
    fun getOrder(orderId: String): Flow<Resource<Order>>

    /**
     * Search order
     */
    suspend fun searchOrder(query: String): Resource<List<Order>>

    /**
     * Insert new order
     */
    suspend fun insertOrder(order: Order)

    /**
     * Update order by [orderId]
     */
    suspend fun updateOrder(orderId: String, order: Order)
}
