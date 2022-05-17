package id.rsdiz.rdshop.domain.repository.ongkir

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.City
import id.rsdiz.rdshop.data.model.Cost
import id.rsdiz.rdshop.data.model.Province
import id.rsdiz.rdshop.data.source.remote.RajaOngkirRemoteDataSource
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IOngkirRepository] from Domain Layer
 */
@Singleton
class OngkirRepository @Inject constructor(
    private val remoteDataSource: RajaOngkirRemoteDataSource
) : IOngkirRepository {
    override suspend fun getCities(): Resource<List<City>> =
        when (
            val response = remoteDataSource.getCities().first()
        ) {
            is ApiResponse.Success -> {
                val data =
                    response.data?.let { remoteDataSource.cityMapper.mapRemoteToEntities(it) }
                Resource.Success(data = data ?: listOf())
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun getCity(cityId: Int): Resource<City> =
        when (
            val response = remoteDataSource.getCity(cityId).first()
        ) {
            is ApiResponse.Success -> {
                val data = response.data?.let { remoteDataSource.cityMapper.mapRemoteToEntity(it) }
                if (data != null) {
                    Resource.Success(data)
                } else {
                    Resource.Error(response.toString(), null)
                }
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun getProvinces(): Resource<List<Province>> =
        when (
            val response = remoteDataSource.getProvinces().first()
        ) {
            is ApiResponse.Success -> {
                val data =
                    response.data?.let { remoteDataSource.provinceMapper.mapRemoteToEntities(it) }
                Resource.Success(data = data ?: listOf())
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun getProvince(provinceId: Int): Resource<Province> =
        when (
            val response = remoteDataSource.getProvince(provinceId).first()
        ) {
            is ApiResponse.Success -> {
                val data =
                    response.data?.let { remoteDataSource.provinceMapper.mapRemoteToEntity(it) }
                if (data != null) {
                    Resource.Success(data)
                } else {
                    Resource.Error(response.toString(), null)
                }
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun getShippingCost(
        origin: Int,
        destination: Int,
        weight: Int,
        courier: String
    ): Resource<List<Cost>> =
        when (
            val response =
                remoteDataSource.getShippingCost(origin, destination, weight, courier).first()
        ) {
            is ApiResponse.Success -> {
                val data = remoteDataSource.costMapper.mapRemoteToEntities(response.data)
                if (data != null) {
                    Resource.Success(data)
                } else {
                    Resource.Error(response.toString(), null)
                }
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }
}
