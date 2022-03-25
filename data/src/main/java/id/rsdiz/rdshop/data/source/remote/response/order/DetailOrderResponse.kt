package id.rsdiz.rdshop.data.source.remote.response.order

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseResponse

/**
 * Model response for detail orders
 */
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

/**
 * Detail Order Responses from API
 */
data class BaseDetailOrderResponse(
    @field:SerializedName("code")
    override val code: Int,
    @field:SerializedName("status")
    override val status: String,
    @field:SerializedName("data")
    override val data: DetailOrderResponse?
) : IBaseResponse<DetailOrderResponse>
