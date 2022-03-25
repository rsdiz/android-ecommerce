package id.rsdiz.rdshop.data.source.remote.response.product

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseResponse

/**
 * Model response for products
 */
data class ProductsResponse(
    @field:SerializedName("count")
    val count: Int,
    @field:SerializedName("next")
    val next: String?,
    @field:SerializedName("previous")
    val previous: String?,
    @field:SerializedName("results")
    val results: List<ProductResponse>
)

/**
 * Products Responses from API
 */
data class BaseProductsResponse(
    @field:SerializedName("code")
    override val code: Int,
    @field:SerializedName("status")
    override val status: String,
    @field:SerializedName("data")
    override val data: ProductsResponse?
) : IBaseResponse<ProductsResponse>
