package id.rsdiz.rdshop.data.source.local

import id.rsdiz.rdshop.data.source.local.entity.OrderEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderWithDetails
import id.rsdiz.rdshop.data.source.local.mapper.OrderMapper
import id.rsdiz.rdshop.data.source.local.room.IDetailOrderDao
import id.rsdiz.rdshop.data.source.local.room.IOrderDao
import id.rsdiz.rdshop.data.source.local.room.IOrderRemoteKeysDao
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderLocalDataSource @Inject constructor(
    val orderDao: IOrderDao,
    val detailOrderDao: IDetailOrderDao,
    val orderRemoteKeysDao: IOrderRemoteKeysDao,
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

    fun update(data: OrderEntity) = orderDao.update(data)

    suspend fun insertAll(list: List<OrderWithDetails>) {
        val listOrder = mutableListOf<OrderEntity>()
        list.forEach {
            listOrder.add(it.order)
            detailOrderDao.insertAll(it.details)
        }
        orderDao.insertAll(listOrder)
    }

    suspend fun insert(data: OrderWithDetails) {
        orderDao.insert(data.order)
        data.details.forEach {
            detailOrderDao.insert(it)
        }
    }

    suspend fun delete(data: OrderWithDetails) {
        data.details.forEach {
            detailOrderDao.delete(it)
        }
        orderDao.delete(data.order)
    }
}
