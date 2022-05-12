package id.rsdiz.rdshop.data.source.remote.response.rajaongkir.cost

import com.google.gson.annotations.SerializedName

/**
 * Model response for Service Cost
 */
data class ServiceCostResponse(
    @field:SerializedName("service")
    val service: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("cost")
    val cost: List<DetailCostResponse>?
)
