package id.rsdiz.rdshop.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Order
 */
@Parcelize
data class Order(
    val orderId: String,
    val userId: String,
    val date: String,
    val amount: Int,
    val shipName: String,
    val shipAddress: String,
    val shippingCost: Int,
    val phone: String,
    val status: Short,
    val trackingNumber: String?,
    val orderDetail: List<OrderDetail>
) : Parcelable
