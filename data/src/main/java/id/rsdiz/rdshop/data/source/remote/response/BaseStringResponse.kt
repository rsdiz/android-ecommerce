package id.rsdiz.rdshop.data.source.remote.response

/**
 * String Responses from API
 */
data class BaseStringResponse(
    override val code: Int,
    override val status: String,
    override val data: String?
) : IBaseResponse<String>
