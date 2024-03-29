package id.rsdiz.rdshop.common

import id.rsdiz.rdshop.data.model.OrderDetail
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.common.base.BaseUiState

data class OrderDetailItemUiState(
    private val product: Product,
    private val orderDetail: OrderDetail
) : BaseUiState() {
    fun getDetailId() = orderDetail.detailId

    fun getProductName() = product.name

    fun getProductId() = orderDetail.productId

    fun getCurrentProductPrice() = orderDetail.price

    fun getOriginalProductPrice() = product.price

    fun getQuantity() = orderDetail.quantity
}
