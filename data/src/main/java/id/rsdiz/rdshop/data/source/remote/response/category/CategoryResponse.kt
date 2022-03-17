package id.rsdiz.rdshop.data.source.remote.response.category

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseResponse

/**
 * Model response for category
 */
data class CategoryResponse(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String
)

/**
 * Category Responses from API
 */
data class BaseCategoryResponse(
    override val code: Int,
    override val status: String,
    override val data: CategoryResponse
) : IBaseResponse<CategoryResponse>
