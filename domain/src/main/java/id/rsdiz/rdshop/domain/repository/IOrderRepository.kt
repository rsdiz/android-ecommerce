package id.rsdiz.rdshop.domain.repository

import androidx.paging.PagingData
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.source.local.entity.OrderWithDetails
import id.rsdiz.rdshop.domain.model.Order
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime

/**
 * Contract for Order Repository
 */
interface IOrderRepository {

    /**
     * Get list of orders
     */
    fun getOrders(): Flow<PagingData<OrderWithDetails>>

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
     * Insert new order to repository
     */
    suspend fun insertOrder(order: Order): Resource<String>

    /**
     * Update order by [orderId]
     */
    suspend fun updateOrder(orderId: String, status: Short, trackingNumber: String = ""): Resource<String>
}
