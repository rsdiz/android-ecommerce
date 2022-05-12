package id.rsdiz.rdshop.domain.usecase.ongkir

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.City
import id.rsdiz.rdshop.data.model.Cost
import id.rsdiz.rdshop.data.model.Province

/**
 * Contract for Ongkir Use Case
 */
interface IOngkirUseCase {

    /**
     * Get list of City
     */
    suspend fun getCities(): Resource<List<City>>

    /**
     * Get specified city by [cityId]
     */
    suspend fun getCity(cityId: Int): Resource<City>

    /**
     * Get list of Province
     */
    suspend fun getProvinces(): Resource<List<Province>>

    /**
     * Get specified province by [provinceId]
     */
    suspend fun getProvince(provinceId: Int): Resource<Province>

    /**
     * Calculate Shipping Cost
     */
    suspend fun getShippingCost(origin: Int, destination: Int, weight: Int, courier: String): Resource<Cost>
}