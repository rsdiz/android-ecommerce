package id.rsdiz.rdshop.data.source.remote.response

/**
 * Contracts for Default Response from API
 */
interface IBaseRajaOngkirResponse<R> {
    val status: RajaOngkirStatusResponse
    val results: R?
}
