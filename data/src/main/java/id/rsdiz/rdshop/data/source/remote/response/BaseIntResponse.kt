package id.rsdiz.rdshop.data.source.remote.response

/**
 * Int Responses from API
 */
data class BaseIntResponse(
    override val code: Int,
    override val status: String,
    override val data: Int?
) : IBaseResponse<Int>
