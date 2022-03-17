package id.rsdiz.rdshop.data.source.remote.response

/**
 * Contracts for Default Response from API
 */
interface IBaseResponse<R> {
    val code: Int
    val status: String
    val data: R
}
