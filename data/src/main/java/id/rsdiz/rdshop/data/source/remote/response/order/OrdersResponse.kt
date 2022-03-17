package id.rsdiz.rdshop.data.source.remote.response.order

import com.google.gson.annotations.SerializedName

data class OrdersResponse(
    @field:SerializedName("count")
    val count: Int,
    @field:SerializedName("next")
    val next: String?,
    @field:SerializedName("previous")
    val previous: String?,
    @field:SerializedName("results")
    val results: List<OrderResponse>
)
