package id.rsdiz.rdshop.data.source.remote

import id.rsdiz.rdshop.data.source.remote.mapper.RajaOngkirMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiRajaOngkirService
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RajaOngkirRemoteDataSource @Inject constructor(
    val apiRajaOngkirService: ApiRajaOngkirService,
    val cityMapper: RajaOngkirMapper.CityMapper,
    val provinceMapper: RajaOngkirMapper.ProvinceMapper,
    val costMapper: RajaOngkirMapper.CostMapper
) {
    suspend fun getProvinces() =
        flow {
            try {
                val response = apiRajaOngkirService.getProvinces()
                when (response.rajaongkir.status.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.rajaongkir.results
                        )
                    )
                    else -> emit(ApiResponse.Error(response.rajaongkir.status.description))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getProvince(provinceId: Int) =
        flow {
            try {
                val response = apiRajaOngkirService.getProvince(
                    id = provinceId
                )
                when (response.rajaongkir.status.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.rajaongkir.results
                        )
                    )
                    else -> emit(ApiResponse.Error(response.rajaongkir.status.description))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getCities() =
        flow {
            try {
                val response = apiRajaOngkirService.getCities()
                when (response.rajaongkir.status.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.rajaongkir.results
                        )
                    )
                    else -> emit(ApiResponse.Error(response.rajaongkir.status.description))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getCity(cityId: Int) =
        flow {
            try {
                val response = apiRajaOngkirService.getCity(
                    id = cityId
                )
                when (response.rajaongkir.status.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.rajaongkir.results
                        )
                    )
                    else -> emit(ApiResponse.Error(response.rajaongkir.status.description))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getShippingCost(origin: Int, destination: Int, weight: Int, courier: String) =
        flow {
            try {
                val response = apiRajaOngkirService.getShippingCost(
                    generateRequestBody(origin.toString()),
                    generateRequestBody(destination.toString()),
                    generateRequestBody(weight.toString()),
                    generateRequestBody(courier)
                )
                when (response.rajaongkir.status.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.rajaongkir.results
                        )
                    )
                    else -> emit(ApiResponse.Error(response.rajaongkir.status.description))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    private fun generateRequestBody(text: String): RequestBody =
        text.toRequestBody("text/plain".toMediaTypeOrNull())
}
