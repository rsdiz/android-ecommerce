package id.rsdiz.rdshop.data.source.remote.response.rajaongkir.cost

import com.google.gson.annotations.SerializedName

/**
 * Model response for Detail Cost
 */
data class DetailCostResponse(
    @field:SerializedName("value")
    val value: Int,
    @field:SerializedName("etd")
    val estimationDay: String?,
    @field:SerializedName("note")
    val note: String?
)
