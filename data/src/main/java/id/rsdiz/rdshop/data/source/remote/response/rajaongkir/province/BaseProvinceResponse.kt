package id.rsdiz.rdshop.data.source.remote.response.rajaongkir.province

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseRajaOngkirResponse
import id.rsdiz.rdshop.data.source.remote.response.RajaOngkirStatusResponse

/**
 * Province Response from RajaOngkir API
 */
data class BaseProvinceResponse(
    @field:SerializedName("status")
    override val status: RajaOngkirStatusResponse,
    @field:SerializedName("results")
    override val results: ProvinceResponse?
) : IBaseRajaOngkirResponse<ProvinceResponse>
