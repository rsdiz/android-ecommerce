package id.rsdiz.rdshop.domain.usecase.order

import androidx.paging.PagingData
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.Order
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime

/**
 * Contract for Order Use Case
 */
interface IOrderUseCase {

    /**
     * Count total row in orders
     */
    suspend fun count(): Resource<Int>

    /**
     * Get list of orders
     */
    fun getOrders(): Flow<PagingData<Order>>

    /**
     * Get newest list of orders
     */
    fun getNewestOrders(): Flow<Resource<List<Order>>>

    /**
     * Get specified order by [orderId]
     */
    fun getOrder(orderId: String): Flow<Resource<Order>>

    /**
     * Get specified order by [userId]
     */
    fun getOrderByUserId(userId: String): Flow<Resource<List<Order>>>

    /**
     * Get specified order by date
     */
    fun getOrderByDate(
        startDate: OffsetDateTime?,
        endDate: OffsetDateTime?
    ): Flow<Resource<List<Order>>>

    /**
     * Insert new order
     */
    suspend fun insertOrder(order: Order): Resource<String>

    /**
     * Update order by [orderId]
     */
    suspend fun updateOrder(
        orderId: String,
        status: Short,
        trackingNumber: String = ""
    ): Resource<String>
}
