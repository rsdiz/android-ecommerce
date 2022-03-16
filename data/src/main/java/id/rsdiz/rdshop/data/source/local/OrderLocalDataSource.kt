package id.rsdiz.rdshop.data.source.local

import id.rsdiz.rdshop.data.source.local.entity.OrderEntity
import id.rsdiz.rdshop.data.source.local.mapper.OrderMapper
import id.rsdiz.rdshop.data.source.local.room.IOrderDao
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderLocalDataSource @Inject constructor(
    private val orderDao: IOrderDao,
    val mapper: OrderMapper
) {
    fun getAllOrder() = orderDao.getAllOrder()

    fun getOrderById(orderId: String) = orderDao.getOrderById(orderId)

    fun getOrderByUserId(userId: String) = orderDao.getOrderByUserId(userId)

    fun getOrderByDate(
        startDate: OffsetDateTime?,
        endDate: OffsetDateTime?
    ) = orderDao.getOrderByDate(startDate, endDate)

    fun getOrderByStatus(status: Short) = orderDao.getOrderByStatus(status)

    fun getOrderDetailById(detailId: String) = orderDao.getOrderDetailById(detailId)

    fun update(data: OrderEntity) = orderDao.update(data)

    suspend fun insertAll(list: List<OrderEntity>) = orderDao.insertAll(list)

    suspend fun insert(data: OrderEntity) = orderDao.insert(data)

    suspend fun delete(data: OrderEntity) = orderDao.delete(data)
}
