package id.rsdiz.rdshop.data.source.remote.response.order

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseResponse

/**
 * Model response for orders
 */
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

/**
 * Orders Responses from API
 */
data class BaseOrdersResponse(
    @field:SerializedName("code")
    override val code: Int,
    @field:SerializedName("status")
    override val status: String,
    @field:SerializedName("data")
    override val data: OrdersResponse?
) : IBaseResponse<OrdersResponse>
