package id.rsdiz.rdshop.data.source.remote.response.rajaongkir.province

import com.google.gson.annotations.SerializedName

/**
 * Model response for Province
 */
data class ProvinceResponse(
    @field:SerializedName("province_id")
    val provinceId: Int,
    @field:SerializedName("province")
    val province: String
)
