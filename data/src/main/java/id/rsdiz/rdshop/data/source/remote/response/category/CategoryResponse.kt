package id.rsdiz.rdshop.data.source.remote.response.category

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String
)
