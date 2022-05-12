package id.rsdiz.rdshop.data.source.remote.network

import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.city.RajaOngkirCititesResponse
import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.city.RajaOngkirCityResponse
import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.cost.RajaOngkirCostResponse
import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.province.RajaOngkirProvinceResponse
import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.province.RajaOngkirProvincesResponse
import retrofit2.http.*

/**
 * Contract for available services from RajaOngkir API Server
 */
interface ApiRajaOngkirService {
    @GET(value = "province")
    suspend fun getProvinces(): RajaOngkirProvincesResponse

    @GET(value = "province")
    suspend fun getProvince(
        @Query(value = "id") id: Int
    ): RajaOngkirProvinceResponse

    @GET(value = "city")
    suspend fun getCities(): RajaOngkirCititesResponse

    @GET(value = "city")
    suspend fun getCity(
        @Query(value = "id") id: Int
    ): RajaOngkirCityResponse

    @Multipart
    @POST(value = "cost")
    suspend fun getShippingCost(
        @Part(value = "origin") origin: Int,
        @Part(value = "destination") destination: Int,
        @Part(value = "weight") weight: Int,
        @Part(value = "courier") courier: String
    ): RajaOngkirCostResponse
}
