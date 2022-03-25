package id.rsdiz.rdshop.data.source.remote.response.category

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseResponse

/**
 * Model response for categories
 */
data class CategoriesResponse(
    val data: List<CategoryResponse>
)

/**
 * Categories Responses from API
 */
data class BaseCategoriesResponse(
    @field:SerializedName("code")
    override val code: Int,
    @field:SerializedName("status")
    override val status: String,
    @field:SerializedName("data")
    override val data: CategoriesResponse?
) : IBaseResponse<CategoriesResponse>
