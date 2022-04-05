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

    fun getOrderMonthNumber() = order.date.monthValue

    fun getFullDate() = StringBuilder(getOrderDate()).append(" ").append(getOrderMonth()).append(" ").append(getOrderYear()).toString()

    fun getOrderTotal() = order.amount.toRupiah()

    fun getShippingCost() = order.shippingCost.toRupiah()

    fun getOrderTime() = order.date.stringTime()

    fun getSimpleOrderId() = StringBuilder("INV")
        .append('/')
        .append(getOrderYear())
        .append(getOrderMonthNumber())
        .append(getOrderDate())
        .append('/')
        .append(order.orderId.split('-')[4])
        .toString()

    fun getOriginalOrderId() = order.orderId

    fun getOrderName() = order.shipName

    fun getOrderStatusValue() = when (order.status) {
        0.toShort() -> "Menunggu Konfirmasi"
        1.toShort() -> "Pesanan Diproses"
        2.toShort() -> "Sedang Dikirim"
        3.toShort() -> "Sampai Tujuan"
        else -> "Tidak Diketahui"
    }

    fun getOrderStatusKey() = order.status

    fun getAddressStreetName() = order.shipAddress.split('|')[0]

    fun getAddressCountry() = order.shipAddress.split('|')[1]

    fun getAddressPostalCode() = order.shipAddress.split('|')[2]

    fun getTrackingNumber() = order.trackingNumber?.split('|')?.get(0)

    fun getTrackingCourier() = order.trackingNumber?.split('|')?.get(1)

    fun getPhone() = order.phone

    fun getUserId() = order.userId

    fun getOrderDetail() = order.orderDetail
}
