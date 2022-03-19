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
    override val code: Int,
    override val status: String,
    override val data: DetailOrderResponse?
) : IBaseResponse<DetailOrderResponse>
