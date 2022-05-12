package id.rsdiz.rdshop.data.source.local.mapper

import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.data.model.ProductImage
import id.rsdiz.rdshop.data.source.local.entity.ProductEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductImageEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductWithImages
import id.rsdiz.rdshop.data.source.local.mapper.base.DataMapper
import javax.inject.Inject

/**
 * Mapper for product entity from data to domain layer
 */
open class ProductMapper @Inject constructor() : DataMapper<ProductWithImages, Product> {
    override fun mapFromEntity(entity: ProductWithImages): Product =
        Product(
            productId = entity.product.productId,
            categoryId = entity.product.categoryId,
            name = entity.product.name,
            weight = entity.product.weight,
            stock = entity.product.stock,
            description = entity.product.description,
            price = entity.product.price,
            image = entity.images.map {
                mapProductImageEntityToProductImage(it)
            },
            isFavorite = entity.product.isFavorite
        )

    override fun mapFromEntities(entities: List<ProductWithImages>): List<Product> =
        entities.map {
            mapFromEntity(it)
        }

    override fun mapToEntity(model: Product): ProductWithImages =
        ProductWithImages(
            product = ProductEntity(
                productId = model.productId,
                categoryId = model.categoryId,
                name = model.name,
                weight = model.weight,
                stock = model.stock,
                description = model.description,
                price = model.price,
                isFavorite = model.isFavorite
            ),
            images = model.image.map {
                mapProductImageToProductImageEntity(model.productId, it)
            }
        )

    fun mapProductImageEntityToProductImage(entity: ProductImageEntity): ProductImage =
        ProductImage(
            imageId = entity.imageId,
            path = entity.path
        )

    fun mapProductImageToProductImageEntity(
        productId: String,
        model: ProductImage
    ): ProductImageEntity =
        ProductImageEntity(
            imageId = model.imageId,
            productId = productId,
            path = model.path
        )
}
