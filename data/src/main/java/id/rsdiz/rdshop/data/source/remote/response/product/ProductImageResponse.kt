package id.rsdiz.rdshop.data.source.remote.response.product

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseResponse

/**
 * Model response for product image
 */
data class ProductImageResponse(
    @field:SerializedName("image_id")
    val imageId: String,
    @field:SerializedName("product_id")
    val productId: String,
    @field:SerializedName("path")
    val path: String
)

/**
 * ProductImage Responses from API
 */
data class BaseProductImageResponse(
    @field:SerializedName("code")
    override val code: Int,
    @field:SerializedName("status")
    override val status: String,
    @field:SerializedName("data")
    override val data: ProductImageResponse?
) : IBaseResponse<ProductImageResponse>
