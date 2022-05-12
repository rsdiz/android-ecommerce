package id.rsdiz.rdshop.data.source.remote.response.rajaongkir.cost

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseRajaOngkirResponse
import id.rsdiz.rdshop.data.source.remote.response.RajaOngkirStatusResponse

/**
 * Cost Response from RajaOngkir API
 */
data class BaseCostResponse(
    @field:SerializedName("status")
    override val status: RajaOngkirStatusResponse,
    @field:SerializedName("results")
    override val results: CostResponse?
) : IBaseRajaOngkirResponse<CostResponse>
