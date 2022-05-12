package id.rsdiz.rdshop.data.source.remote.response.rajaongkir.province

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseRajaOngkirResponse
import id.rsdiz.rdshop.data.source.remote.response.RajaOngkirStatusResponse

/**
 * Provinces Response from RajaOngkir API
 */
data class BaseProvincesResponse(
    @field:SerializedName("status")
    override val status: RajaOngkirStatusResponse,
    @field:SerializedName("results")
    override val results: List<ProvinceResponse>?
) : IBaseRajaOngkirResponse<List<ProvinceResponse>>
