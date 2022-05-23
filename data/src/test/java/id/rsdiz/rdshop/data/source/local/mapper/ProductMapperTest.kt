package id.rsdiz.rdshop.data.source.local.mapper

import id.rsdiz.rdshop.data.factory.DataFactory
import id.rsdiz.rdshop.data.factory.ProductFactory
import id.rsdiz.rdshop.data.model.Product
import id.rsdiz.rdshop.data.source.local.entity.ProductWithImages
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class ProductMapperTest {
    private lateinit var productMapper: ProductMapper

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        productMapper = ProductMapper()
    }

    /**
     * Function for testing [ProductMapper.mapFromEntities] method
     */
    @Test
    fun mapFromEntity() {
        val productEntity = ProductFactory.makeProductWithImage(DataFactory.randomString())
        val product = productMapper.mapFromEntity(productEntity)

        assertProductDataEquals(productEntity, product)
    }

    /**
     * Function for testing [ProductMapper.mapToEntity] method
     */
    @Test
    fun mapToEntity() {
        val product = ProductFactory.makeProduct(DataFactory.randomString())
        val productEntity = productMapper.mapToEntity(product)

        assertProductDataEquals(productEntity, product)
    }

    /**
     * Function for testing [UserMapper.mapFromEntities] method
     */
    @Test
    fun mapFromEntities() {
        val productEntities =
            ProductFactory.makeProductWithImageList(categoryId = DataFactory.randomString())
        val productList = productMapper.mapFromEntities(productEntities)

        repeat(productEntities.size) {
            assertProductDataEquals(productEntities[it], productList[it])
        }
    }

    private fun assertProductDataEquals(entity: ProductWithImages, product: Product) {
        assertEquals(entity.product.productId, product.productId)
        assertEquals(entity.product.categoryId, product.categoryId)
        assertEquals(entity.product.name, product.name)
        assertEquals(entity.product.description, product.description)
        assertEquals(entity.product.price, product.price)
        assertEquals(entity.product.weight, product.weight)
        assertEquals(entity.product.stock, product.stock)

        repeat(entity.images.size) {
            assertEquals(entity.images[it].imageId, product.image[it].imageId)
            assertEquals(entity.images[it].productId, product.productId)
            assertEquals(entity.images[it].path, product.image[it].path)
        }
    }
}
