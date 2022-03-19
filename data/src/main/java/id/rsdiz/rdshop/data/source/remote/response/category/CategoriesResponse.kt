package id.rsdiz.rdshop.data.source.remote.response.category

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
    override val code: Int,
    override val status: String,
    override val data: CategoriesResponse?
) : IBaseResponse<CategoriesResponse>
