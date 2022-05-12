package id.rsdiz.rdshop.data.source.remote.response.rajaongkir.city

import com.google.gson.annotations.SerializedName

/**
 * Model response for city
 */
data class CityResponse(
    @field:SerializedName("city_id")
    val cityId: Int,
    @field:SerializedName("province_id")
    val provinceId: Int,
    @field:SerializedName("province")
    val province: String,
    @field:SerializedName("type")
    val type: String,
    @field:SerializedName("city_name")
    val cityName: String,
    @field:SerializedName("postal_code")
    val postalCode: Int
)
