package id.rsdiz.rdshop.data.source.remote.response.rajaongkir.cost

import com.google.gson.annotations.SerializedName

/**
 * Model response for cost
 */
data class CostResponse(
    @field:SerializedName("code")
    val code: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("costs")
    val costs: List<ServiceCostResponse>?
)
