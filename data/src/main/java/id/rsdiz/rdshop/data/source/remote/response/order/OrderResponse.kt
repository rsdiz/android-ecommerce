package id.rsdiz.rdshop.data.source.remote.response.order

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("user_id")
    val userId: String,
    @field:SerializedName("date")
    val date: String,
    @field:SerializedName("amount")
    val amount: Int,
    @field:SerializedName("ship_name")
    val shipName: String,
    @field:SerializedName("ship_address")
    val shipAddress: String,
    @field:SerializedName("shipping_cost")
    val shippingCost: Int,
    @field:SerializedName("phone")
    val phone: String,
    @field:SerializedName("status")
    val status: Short,
    @field:SerializedName("tracking_number")
    val trackingNumber: String?,
    @field:SerializedName("detail_order")
    val detailOrder: List<DetailOrderResponse>
)
