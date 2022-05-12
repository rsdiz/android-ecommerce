package id.rsdiz.rdshop.data.source.remote.mapper

import id.rsdiz.rdshop.data.model.*
import id.rsdiz.rdshop.data.source.remote.mapper.base.EntityMapper
import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.city.CityResponse
import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.cost.CostResponse
import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.cost.DetailCostResponse
import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.cost.ServiceCostResponse
import id.rsdiz.rdshop.data.source.remote.response.rajaongkir.province.ProvinceResponse
import javax.inject.Inject

/**
 * Maps a RajaOngkir API to our model
 */
object RajaOngkirMapper {

    /**
     * Maps a CityResponse to our Model
     */
    open class CityMapper @Inject constructor() : EntityMapper<CityResponse, City> {
        override fun mapRemoteToEntity(remote: CityResponse): City = City(
            cityId = remote.cityId,
            provinceId = remote.provinceId,
            province = remote.province,
            type = remote.type,
            cityName = remote.cityName,
            postalCode = remote.postalCode
        )

        override fun mapRemoteToEntities(remotes: List<CityResponse>): List<City> {
            val list = mutableListOf<City>()

            remotes.map {
                list.add(mapRemoteToEntity(it))
            }

            return list
        }
    }

    /**
     * Maps a ProvinceResponse to our Model
     */
    open class ProvinceMapper @Inject constructor() : EntityMapper<ProvinceResponse, Province> {
        override fun mapRemoteToEntity(remote: ProvinceResponse): Province =
            Province(
                provinceId = remote.provinceId,
                province = remote.province
            )

        override fun mapRemoteToEntities(remotes: List<ProvinceResponse>): List<Province> {
            val list = mutableListOf<Province>()

            remotes.map {
                list.add(mapRemoteToEntity(it))
            }

            return list
        }
    }

    /**
     * Maps a CostResponse to our model
     */
    open class CostMapper @Inject constructor() : EntityMapper<CostResponse, Cost> {
        override fun mapRemoteToEntity(remote: CostResponse): Cost =
            Cost(
                code = remote.code,
                name = remote.name,
                costs = mapCostsToEntities(remote.costs)
            )

        override fun mapRemoteToEntities(remotes: List<CostResponse>): List<Cost> {
            val list = mutableListOf<Cost>()

            remotes.map {
                list.add(mapRemoteToEntity(it))
            }

            return list
        }

        private fun mapCostToEntity(remote: ServiceCostResponse): ServiceCost =
            ServiceCost(
                service = remote.service,
                description = remote.description,
                cost = mapDetailCostsToEntities(remote.cost)
            )

        private fun mapCostsToEntities(remotes: List<ServiceCostResponse>?): List<ServiceCost> {
            val list = mutableListOf<ServiceCost>()

            remotes?.map {
                list.add(mapCostToEntity(it))
            }

            return list
        }

        private fun mapDetailCostToEntity(remote: DetailCostResponse): DetailCost =
            DetailCost(
                value = remote.value,
                estimationDay = remote.estimationDay,
                note = remote.note
            )

        private fun mapDetailCostsToEntities(remotes: List<DetailCostResponse>?): List<DetailCost> {
            val list = mutableListOf<DetailCost>()

            remotes?.map {
                list.add(mapDetailCostToEntity(it))
            }

            return list
        }
    }
}
