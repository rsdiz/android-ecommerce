package id.rsdiz.rdshop.domain.repository

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.Order
import kotlinx.coroutines.flow.Flow

/**
 * Contract for Order Repository
 */
interface IOrderRepository {

    /**
     * Get list of orders
     */
    fun getOrders(): Flow<Resource<List<Order>>>

    /**
     * Get specified order by [orderId]
     */
    fun getOrder(orderId: String): Flow<Resource<Order>>

    /**
     * Search order in repository
     */
    suspend fun searchOrder(query: String): Resource<List<Order>>

    /**
     * Insert new order to repository
     */
    suspend fun insertOrder(order: Order)

    /**
     * Update order by [orderId]
     */
    suspend fun updateOrder(orderId: String, order: Order)
}
