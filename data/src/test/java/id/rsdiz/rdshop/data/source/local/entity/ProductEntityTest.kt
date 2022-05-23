package id.rsdiz.rdshop.data.source.local.entity

import id.rsdiz.rdshop.data.factory.DataFactory
import id.rsdiz.rdshop.data.factory.ProductFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class ProductEntityTest {
    private lateinit var productEntity: ProductEntity

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        productEntity = ProductFactory.makeProductEntity(categoryId = DataFactory.randomString())
    }

    /**
     * Test an Equation for object symmetric
     */
    @Test
    fun testEqualsSymmetric() {
        val productEntity1 = ProductEntity(
            productEntity.productId,
            productEntity.categoryId,
            productEntity.name,
            productEntity.weight,
            productEntity.stock,
            productEntity.description,
            productEntity.price,
            productEntity.isFavorite
        )

        val productEntity2 = ProductEntity(
            productEntity.productId,
            productEntity.categoryId,
            productEntity.name,
            productEntity.weight,
            productEntity.stock,
            productEntity.description,
            productEntity.price,
            productEntity.isFavorite
        )

        assertTrue(productEntity1 == productEntity2 && productEntity2 == productEntity1)
        assertEquals(productEntity1.hashCode(), productEntity2.hashCode())
    }
}
