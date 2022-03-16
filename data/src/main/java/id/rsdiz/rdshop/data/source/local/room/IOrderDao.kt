package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Query
import androidx.room.Transaction
import id.rsdiz.rdshop.data.source.local.entity.OrderEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderWithDetailsAndProduct
import id.rsdiz.rdshop.data.source.local.room.base.IBaseDao
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime

/**
 * Contracts how application interacts with stored data in [OrderEntity]
 */
interface IOrderDao : IBaseDao<OrderEntity> {
    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrder(): Flow<List<OrderWithDetailsAndProduct>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderById(orderId: String): Flow<OrderWithDetailsAndProduct>

    @Transaction
    @Query("SELECT * FROM orders WHERE userId = :userId")
    fun getOrderByUserId(userId: String): Flow<List<OrderWithDetailsAndProduct>>

    @Transaction
    @Query("SELECT * FROM orders WHERE date BETWEEN date(:startDate) AND date(:endDate) ORDER BY date")
    fun getOrderByDate(
        startDate: OffsetDateTime? = OffsetDateTime.MIN,
        endDate: OffsetDateTime? = OffsetDateTime.MAX
    ): Flow<List<OrderWithDetailsAndProduct>>

    @Transaction
    @Query("SELECT * FROM orders WHERE status = :status")
    fun getOrderByStatus(status: Short): Flow<List<OrderWithDetailsAndProduct>>
}
