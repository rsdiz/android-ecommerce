package id.rsdiz.rdshop.data.source.remote.response.product

import com.google.gson.annotations.SerializedName

data class ProductImageResponse(
    @field:SerializedName("image_id")
    val imageId: String,
    @field:SerializedName("product_id")
    val productId: String,
    @field:SerializedName("path")
    val path: String
)
