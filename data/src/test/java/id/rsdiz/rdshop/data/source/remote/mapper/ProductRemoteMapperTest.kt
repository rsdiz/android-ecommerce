package id.rsdiz.rdshop.data.source.remote.mapper

import id.rsdiz.rdshop.data.factory.DataFactory
import id.rsdiz.rdshop.data.factory.ProductFactory
import id.rsdiz.rdshop.data.source.local.entity.ProductWithImages
import id.rsdiz.rdshop.data.source.remote.response.product.ProductResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class ProductRemoteMapperTest {
    private lateinit var productRemoteMapper: ProductRemoteMapper

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        productRemoteMapper = ProductRemoteMapper()
    }

    /**
     * Function for testing [ProductRemoteMapper.mapRemoteToEntity] method
     */
    @Test
    fun mapRemoteToEntity() {
        val productRemote = ProductFactory.makeProductResponse(DataFactory.randomString())
        val productEntity = productRemoteMapper.mapRemoteToEntity(productRemote)

        assertProductDataEquals(productEntity, productRemote)
    }

    /**
     * Function for testing [ProductRemoteMapper.mapRemoteToEntities] method
     */
    @Test
    fun mapRemoteToEntities() {
        val productRemoteList = ProductFactory.makeProductResponseList(categoryId = null)
        val productEntities = productRemoteMapper.mapRemoteToEntities(productRemoteList)

        repeat(productRemoteList.size) {
            assertProductDataEquals(productEntities[it], productRemoteList[it])
        }
    }

    private fun assertProductDataEquals(entity: ProductWithImages, response: ProductResponse) {
        assertEquals(entity.product.productId, response.id)
        assertEquals(entity.product.categoryId, response.categoryId)
        assertEquals(entity.product.name, response.name)
        assertEquals(entity.product.description, response.description)
        assertEquals(entity.product.price, response.price)
        assertEquals(entity.product.weight, response.weight)
        assertEquals(entity.product.stock, response.stock)

        repeat(entity.images.size) {
            assertEquals(entity.images[it].imageId, response.images!![it].imageId)
            assertEquals(entity.images[it].productId, response.images!![it].productId)
            assertEquals(entity.images[it].path, response.images!![it].path)
        }
    }
}