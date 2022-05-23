package id.rsdiz.rdshop.data.factory

import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.data.model.OrderDetail
import id.rsdiz.rdshop.data.source.local.entity.DetailOrderEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderWithDetails
import id.rsdiz.rdshop.data.source.remote.response.order.DetailOrderResponse
import id.rsdiz.rdshop.data.source.remote.response.order.OrderResponse
import org.threeten.bp.OffsetDateTime

/**
 * This class is used to generate [Order], [OrderEntity], and [OrderResponse] for usage on tests
 */
object OrderFactory {
    /**
     * Return a [Order]
     */
    fun makeOrder(): Order =
        Order(
            orderId = DataFactory.randomString(),
            userId = DataFactory.randomString(),
            date = OffsetDateTime.MIN,
            amount = DataFactory.randomInt(),
            shipName = DataFactory.randomString(),
            shipAddress = DataFactory.randomString(),
            shippingCost = DataFactory.randomInt(),
            phone = DataFactory.randomString(),
            status = DataFactory.randomInt().toShort(),
            trackingNumber = DataFactory.randomString(),
            orderDetail = makeOrderDetailList(count = 2)
        )

    /**
     * Return an list of [Order]
     */
    fun makeOrderList(count: Int = 5): List<Order> {
        val list = mutableListOf<Order>()
        repeat(count) {
            list.add(makeOrder())
        }
        return list
    }

    /**
     * Return an list of [OrderDetail]
     */
    fun makeOrderDetailList(count: Int = 5): List<OrderDetail> {
        val list = mutableListOf<OrderDetail>()
        repeat(count) {
            list.add(makeOrderDetail())
        }
        return list
    }

    /**
     * Return a [OrderDetail]
     */
    fun makeOrderDetail(): OrderDetail =
        OrderDetail(
            detailId = DataFactory.randomString(),
            productId = DataFactory.randomString(),
            price = DataFactory.randomInt(),
            quantity = DataFactory.randomInt()
        )

    /**
     * Return a [OrderEntity]
     */
    fun makeOrderEntity(orderId: String?): OrderEntity =
        OrderEntity(
            orderId = orderId ?: DataFactory.randomString(),
            userId = DataFactory.randomString(),
            date = OffsetDateTime.MIN,
            amount = DataFactory.randomInt(),
            shipName = DataFactory.randomString(),
            shipAddress = DataFactory.randomString(),
            shippingCost = DataFactory.randomInt(),
            phone = DataFactory.randomString(),
            status = DataFactory.randomInt().toShort(),
            trackingNumber = DataFactory.randomString()
        )

    /**
     * Return an list of [OrderEntity]
     */
    fun makeOrderEntityList(count: Int = 5, orderId: String? = null): List<OrderEntity> {
        val list = mutableListOf<OrderEntity>()
        repeat(count) {
            list.add(makeOrderEntity(orderId))
        }
        return list
    }

    /**
     * Return a [DetailOrderEntity]
     */
    fun makeDetailOrderEntity(orderId: String): DetailOrderEntity =
        DetailOrderEntity(
            detailId = DataFactory.randomString(),
            orderId = orderId,
            productId = DataFactory.randomString(),
            price = DataFactory.randomInt(),
            quantity = DataFactory.randomInt()
        )

    /**
     * Return an list of [DetailOrderEntity]
     */
    fun makeDetailOrderEntities(count: Int = 5, orderId: String): List<DetailOrderEntity> {
        val list = mutableListOf<DetailOrderEntity>()
        repeat(count) {
            list.add(makeDetailOrderEntity(orderId))
        }
        return list
    }

    /**
     * Return a [OrderWithDetails]
     */
    fun makeOrderWithDetails(): OrderWithDetails {
        val orderId = DataFactory.randomString()
        return OrderWithDetails(
            order = makeOrderEntity(orderId),
            details = makeDetailOrderEntities(count = 3, orderId = orderId)
        )
    }

    /**
     * Return an list of [OrderWithDetails]
     */
    fun makeOrderWithDetailsList(count: Int = 5): List<OrderWithDetails> {
        val list = mutableListOf<OrderWithDetails>()
        repeat(count) {
            list.add(makeOrderWithDetails())
        }
        return list
    }

    /**
     * Return a [OrderResponse]
     */
    fun makeOrderResponse(): OrderResponse =
        OrderResponse(
            id = DataFactory.randomString(),
            userId = DataFactory.randomString(),
            date = OffsetDateTime.MIN.toString(),
            amount = DataFactory.randomInt(),
            shipName = DataFactory.randomString(),
            shipAddress = DataFactory.randomString(),
            shippingCost = DataFactory.randomInt(),
            phone = DataFactory.randomString(),
            status = DataFactory.randomInt().toShort(),
            trackingNumber = DataFactory.randomString(),
            detailOrder = makeOrderDetailResponseList(count = 2)
        )

    fun makeOrderResponseList(count: Int = 5): List<OrderResponse> {
        val list = mutableListOf<OrderResponse>()
        repeat(count) {
            list.add(makeOrderResponse())
        }
        return list
    }

    /**
     * Return an list of [DetailOrderResponse]
     */
    fun makeOrderDetailResponseList(count: Int = 5): List<DetailOrderResponse> {
        val list = mutableListOf<DetailOrderResponse>()
        repeat(count) {
            list.add(makeOrderDetailResponse())
        }
        return list
    }

    /**
     * Return a [DetailOrderResponse]
     */
    fun makeOrderDetailResponse(): DetailOrderResponse =
        DetailOrderResponse(
            id = DataFactory.randomString(),
            productId = DataFactory.randomString(),
            price = DataFactory.randomInt(),
            quantity = DataFactory.randomInt()
        )
}
