package id.rsdiz.rdshop.domain.usecase.ongkir

import id.rsdiz.rdshop.domain.repository.ongkir.IOngkirRepository
import javax.inject.Inject

/**
 * Implement of [IOngkirUseCase]
 */
class OngkirUseCase @Inject constructor(
    private val repository: IOngkirRepository
) : IOngkirUseCase {
    override suspend fun getCities() = repository.getCities()

    override suspend fun getCity(cityId: Int) = repository.getCity(cityId)

    override suspend fun getProvinces() = repository.getProvinces()

    override suspend fun getProvince(provinceId: Int) = repository.getProvince(provinceId)

    override suspend fun getShippingCost(
        origin: Int,
        destination: Int,
        weight: Int,
        courier: String
    ) = repository.getShippingCost(origin, destination, weight, courier)
}
