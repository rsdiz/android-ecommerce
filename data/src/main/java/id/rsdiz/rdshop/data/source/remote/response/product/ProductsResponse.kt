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
    override val code: Int,
    override val status: String,
    override val data: ProductsResponse?
) : IBaseResponse<ProductsResponse>
