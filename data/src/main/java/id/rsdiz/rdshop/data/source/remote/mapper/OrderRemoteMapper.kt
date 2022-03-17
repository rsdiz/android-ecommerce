package id.rsdiz.rdshop.data.source.remote.mapper

import id.rsdiz.rdshop.base.utils.toOffsetDateTime
import id.rsdiz.rdshop.data.source.local.entity.DetailOrderEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderEntity
import id.rsdiz.rdshop.data.source.local.entity.OrderWithDetails
import id.rsdiz.rdshop.data.source.remote.mapper.base.EntityMapper
import id.rsdiz.rdshop.data.source.remote.response.order.DetailOrderResponse
import id.rsdiz.rdshop.data.source.remote.response.order.OrderResponse
import javax.inject.Inject

/**
 * Maps a Category from remote (API) to our model
 */
open class OrderRemoteMapper @Inject constructor() : EntityMapper<OrderResponse, OrderWithDetails> {
    override fun mapRemoteToEntity(remote: OrderResponse): OrderWithDetails =
        OrderWithDetails(
            order = mapOrderRemoteToOrderEntity(remote),
            details = mapOrderRemotesToDetailOrderEntities(remote.id, remote.detailOrder)
        )

    private fun mapOrderRemotesToDetailOrderEntities(
        orderId: String,
        detailOrder: List<DetailOrderResponse>
    ): List<DetailOrderEntity> =
        detailOrder.map {
            mapDetailOrderRemoteToDetailOrderEntity(orderId, it)
        }

    private fun mapDetailOrderRemoteToDetailOrderEntity(
        orderId: String,
        detailOrderResponse: DetailOrderResponse
    ): DetailOrderEntity =
        DetailOrderEntity(
            detailId = detailOrderResponse.id,
            orderId = orderId,
            productId = detailOrderResponse.productId,
            price = detailOrderResponse.price,
            quantity = detailOrderResponse.quantity
        )

    private fun mapOrderRemoteToOrderEntity(remote: OrderResponse): OrderEntity =
        OrderEntity(
            orderId = remote.id,
            userId = remote.userId,
            date = remote.date.toOffsetDateTime(),
            amount = remote.amount,
            shipName = remote.shipName,
            shipAddress = remote.shipAddress,
            shippingCost = remote.shippingCost,
            phone = remote.phone,
            status = remote.status,
            trackingNumber = remote.trackingNumber
        )

    override fun mapRemoteToEntities(remotes: List<OrderResponse>): List<OrderWithDetails> {
        val list = mutableListOf<OrderWithDetails>()

        remotes.map {
            list.add(mapRemoteToEntity(it))
        }

        return list
    }
}
