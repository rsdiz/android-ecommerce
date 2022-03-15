package id.rsdiz.rdshop.data.source.remote.response.order

import com.google.gson.annotations.SerializedName

data class DetailOrderResponse(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("product_id")
    val productId: String,
    @field:SerializedName("price")
    val price: Int,
    @field:SerializedName("quantity")
    val quantity: Int
)
