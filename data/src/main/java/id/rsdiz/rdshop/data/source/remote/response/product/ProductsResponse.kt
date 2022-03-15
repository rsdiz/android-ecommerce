package id.rsdiz.rdshop.data.source.remote.response.product

import com.google.gson.annotations.SerializedName

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
