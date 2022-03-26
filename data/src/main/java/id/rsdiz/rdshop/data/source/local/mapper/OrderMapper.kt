package id.rsdiz.rdshop.data.source.local.mapper

import id.rsdiz.rdshop.data.source.local.entity.DetailOrderEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderWithDetails
import id.rsdiz.rdshop.data.source.local.mapper.base.DataMapper
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.data.model.OrderDetail
import javax.inject.Inject

/**
 * Mapper for order entity from data to domain layer
 */
open class OrderMapper @Inject constructor() : DataMapper<OrderWithDetails, Order> {
    override fun mapFromEntity(entity: OrderWithDetails): Order =
        Order(
            orderId = entity.order.orderId,
            userId = entity.order.userId,
            date = entity.order.date,
            amount = entity.order.amount,
            shipName = entity.order.shipName,
            shipAddress = entity.order.shipAddress,
            shippingCost = entity.order.shippingCost,
            phone = entity.order.phone,
            status = entity.order.status,
            trackingNumber = entity.order.trackingNumber,
            orderDetail = entity.details.map {
                mapDetailOrderEntityToOrderDetail(it)
            }
        )

    override fun mapFromEntities(entities: List<OrderWithDetails>): List<Order> =
        entities.map {
            mapFromEntity(it)
        }

    override fun mapToEntity(model: Order): OrderWithDetails =
        OrderWithDetails(
            order = OrderEntity(
                orderId = model.orderId,
                userId = model.userId,
                date = model.date,
                amount = model.amount,
                shipName = model.shipName,
                shipAddress = model.shipAddress,
                shippingCost = model.shippingCost,
                phone = model.phone,
                status = model.status,
                trackingNumber = model.trackingNumber
            ),
            details = model.orderDetail.map {
                mapOrderDetailToDetailOrderEntity(model.orderId, it)
            }
        )

    private fun mapOrderDetailToDetailOrderEntity(orderId: String, model: OrderDetail): DetailOrderEntity =
        DetailOrderEntity(
            detailId = model.detailId,
            orderId = orderId,
            productId = model.productId,
            price = model.price,
            quantity = model.quantity
        )

    private fun mapDetailOrderEntityToOrderDetail(entity: DetailOrderEntity): OrderDetail =
        OrderDetail(
            detailId = entity.detailId,
            productId = entity.productId,
            price = entity.price,
            quantity = entity.quantity
        )
}
