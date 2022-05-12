package id.rsdiz.rdshop.data.source.remote.response.rajaongkir.city

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseRajaOngkirResponse
import id.rsdiz.rdshop.data.source.remote.response.RajaOngkirStatusResponse

/**
 * Cities Response from RajaOngkir API
 */
data class BaseCitiesResponse(
    @field:SerializedName("status")
    override val status: RajaOngkirStatusResponse,
    @field:SerializedName("results")
    override val results: List<CityResponse>?
) : IBaseRajaOngkirResponse<List<CityResponse>>
