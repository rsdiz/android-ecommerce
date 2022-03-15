package id.rsdiz.rdshop.data.source.remote.response.product

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("category_id")
    val categoryId: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("weight")
    val weight: Float,
    @field:SerializedName("stock")
    val stock: Int,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("price")
    val price: Int,
    @field:SerializedName("images")
    val images: List<ProductImageResponse>?
)
