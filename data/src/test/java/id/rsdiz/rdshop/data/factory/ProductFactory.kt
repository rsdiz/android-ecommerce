package id.rsdiz.rdshop.data.factory

import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.data.model.ProductImage
import id.rsdiz.rdshop.data.source.local.entity.ProductEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductImageEntity
import id.rsdiz.rdshop.data.source.local.entity.ProductWithImages
import id.rsdiz.rdshop.data.source.remote.response.product.ProductImageResponse
import id.rsdiz.rdshop.data.source.remote.response.product.ProductResponse

/**
 * This class is used to generate [Product], [ProductEntity], and [ProductResponse] for usage on tests
 */
object ProductFactory {
    /**
     * Return a [Product]
     */
    fun makeProduct(categoryId: String): Product =
        Product(
            productId = DataFactory.randomString(),
            categoryId = categoryId,
            name = DataFactory.randomString(),
            weight = DataFactory.randomDouble().toFloat(),
            stock = DataFactory.randomInt(),
            description = DataFactory.randomString(),
            price = DataFactory.randomInt(),
            image = makeProductImageList(),
            isFavorite = false
        )

    /**
     * Return an list of [Product]
     */
    fun makeProductList(count: Int = 5, categoryId: String): List<Product> {
        val list = mutableListOf<Product>()
        repeat(count) {
            list.add(makeProduct(categoryId))
        }
        return list
    }

    /**
     * Return a [ProductImage]
     */
    fun makeProductImage(): ProductImage =
        ProductImage(
            imageId = DataFactory.randomString(),
            path = DataFactory.randomString()
        )

    /**
     * Return an list of [ProductImage]
     */
    fun makeProductImageList(count: Int = 3): List<ProductImage> {
        val list = mutableListOf<ProductImage>()
        repeat(count) {
            list.add(makeProductImage())
        }
        return list
    }

    /**
     * Return a [ProductEntity]
     */
    fun makeProductEntity(categoryId: String?): ProductEntity =
        ProductEntity(
            productId = DataFactory.randomString(),
            categoryId = categoryId ?: DataFactory.randomString(),
            name = DataFactory.randomString(),
            weight = DataFactory.randomDouble().toFloat(),
            stock = DataFactory.randomInt(),
            description = DataFactory.randomString(),
            price = DataFactory.randomInt(),
            isFavorite = false
        )

    /**
     * Return a [ProductImageEntity]
     */
    fun makeProductImageEntity(productId: String): ProductImageEntity =
        ProductImageEntity(
            imageId = DataFactory.randomString(),
            productId = productId,
            path = DataFactory.randomString()
        )

    /**
     * Return an list of [ProductImageEntity]
     */
    fun makeProductImageEntityList(count: Int = 3, productId: String): List<ProductImageEntity> {
        val list = mutableListOf<ProductImageEntity>()
        repeat(count) {
            list.add(makeProductImageEntity(productId))
        }
        return list
    }

    /**
     * Return a [ProductWithImages]
     */
    fun makeProductWithImage(categoryId: String): ProductWithImages {
        val product = makeProductEntity(categoryId)
        val images = makeProductImageEntityList(productId = product.productId)
        return ProductWithImages(product, images)
    }

    /**
     * Return an list of [ProductWithImages]
     */
    fun makeProductWithImageList(count: Int = 3, categoryId: String): List<ProductWithImages> {
        val list = mutableListOf<ProductWithImages>()
        repeat(count) {
            list.add(makeProductWithImage(categoryId))
        }
        return list
    }

    /**
     * Return a [ProductResponse]
     */
    fun makeProductResponse(categoryId: String?): ProductResponse {
        val productId = DataFactory.randomString()
        return ProductResponse(
            id = productId,
            categoryId = categoryId ?: DataFactory.randomString(),
            name = DataFactory.randomString(),
            weight = DataFactory.randomDouble().toFloat(),
            stock = DataFactory.randomInt(),
            description = DataFactory.randomString(),
            price = DataFactory.randomInt(),
            images = makeProductImageResponseList(count = 3, productId = productId)
        )
    }

    /**
     * Return an list of [ProductResponse]
     */
    fun makeProductResponseList(count: Int = 3, categoryId: String?): List<ProductResponse> {
        val list = mutableListOf<ProductResponse>()
        repeat(count) {
            list.add(makeProductResponse(categoryId))
        }
        return list
    }

    /**
     * Return a [ProductImageResponse]
     */
    fun makeProductImageResponse(productId: String): ProductImageResponse =
        ProductImageResponse(
            imageId = DataFactory.randomString(),
            productId = productId,
            path = DataFactory.randomString()
        )

    /**
     * Return an list of [ProductImageResponse]
     */
    fun makeProductImageResponseList(
        count: Int = 3,
        productId: String
    ): List<ProductImageResponse> {
        val list = mutableListOf<ProductImageResponse>()
        repeat(count) {
            list.add(makeProductImageResponse(productId))
        }
        return list
    }
}
