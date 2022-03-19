package id.rsdiz.rdshop.data.source.remote.mapper

import id.rsdiz.rdshop.data.source.local.entity.ProductEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductImageEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductWithImages
import id.rsdiz.rdshop.data.source.remote.mapper.base.EntityMapper
import id.rsdiz.rdshop.data.source.remote.response.product.ProductImageResponse
import id.rsdiz.rdshop.data.source.remote.response.product.ProductResponse
import javax.inject.Inject

/**
 * Maps a Category from remote (API) to our model
 */
open class ProductRemoteMapper @Inject constructor() :
    EntityMapper<ProductResponse, ProductWithImages> {
    override fun mapRemoteToEntity(remote: ProductResponse): ProductWithImages = ProductWithImages(
        product = mapProductRemoteToProductEntity(remote),
        images = mapProductImageRemotesToProductImageEntities(remote.images) ?: listOf()
    )

    override fun mapRemoteToEntities(remotes: List<ProductResponse>): List<ProductWithImages> {
        val list = mutableListOf<ProductWithImages>()

        remotes.map {
            list.add(mapRemoteToEntity(it))
        }

        return list
    }

    private fun mapProductRemoteToProductEntity(remote: ProductResponse): ProductEntity =
        ProductEntity(
            productId = remote.id,
            categoryId = remote.categoryId,
            name = remote.name,
            weight = remote.weight,
            stock = remote.stock,
            description = remote.description,
            price = remote.price
        )

    fun mapProductImageRemoteToProductImageEntity(
        remote: ProductImageResponse
    ): ProductImageEntity =
        ProductImageEntity(
            imageId = remote.imageId,
            productId = remote.productId,
            path = remote.path
        )

    fun mapProductImageRemotesToProductImageEntities(
        remote: List<ProductImageResponse>?
    ): List<ProductImageEntity>? =
        remote?.map {
            mapProductImageRemoteToProductImageEntity(it)
        }
}
