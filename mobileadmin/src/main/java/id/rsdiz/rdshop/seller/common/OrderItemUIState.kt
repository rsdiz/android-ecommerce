package id.rsdiz.rdshop.seller.common

import id.rsdiz.rdshop.base.utils.stringTime
import id.rsdiz.rdshop.base.utils.toRupiah
import id.rsdiz.rdshop.data.model.Order
import id.rsdiz.rdshop.seller.common.base.BaseUiState

data class OrderItemUIState(
    private val order: Order
) : BaseUiState() {
    fun getOrderYear() = order.date.year.toString()

    fun getOrderDate() = order.date.dayOfMonth.toString()

    fun getOrderMonth() = order.date.month.toString().slice(0..2)

    fun getOrderTotal() = order.amount.toRupiah() + ",-"

    fun getOrderTime() = order.date.stringTime()

    fun getSimpleOrderId() = order.orderId.split('-')[4]

    fun getOriginalOrderId() = order.orderId

    fun getOrderName() = order.shipName

    fun getOrderStatus() = when (order.status) {
        0.toShort() -> "Menunggu Konfirmasi"
        1.toShort() -> "Pesanan Diproses"
        2.toShort() -> "Sedang Dikirim"
        3.toShort() -> "Sampai Tujuan"
        else -> "Tidak Diketahui"
    }

    fun getOrderAddress() = order.shipAddress

    fun getTrackingNumber() = order.trackingNumber

    fun getPhone() = order.phone

    fun getUserId() = order.userId

    fun getOrderDetail() = order.orderDetail
}
