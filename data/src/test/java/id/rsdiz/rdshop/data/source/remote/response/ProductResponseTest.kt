package id.rsdiz.rdshop.data.source.remote.response

import id.rsdiz.rdshop.data.factory.DataFactory
import id.rsdiz.rdshop.data.factory.ProductFactory
import id.rsdiz.rdshop.data.source.remote.response.product.ProductResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class ProductResponseTest {
    private lateinit var productResponse: ProductResponse

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        productResponse = ProductFactory.makeProductResponse(DataFactory.randomString())
    }

    /**
     * Test an Equation for object symmetric
     */
    @Test
    fun testEqualsSymmetric() {
        val productResponse1 = ProductResponse(
            productResponse.id,
            productResponse.categoryId,
            productResponse.name,
            productResponse.weight,
            productResponse.stock,
            productResponse.description,
            productResponse.price,
            productResponse.images
        )

        val productResponse2 = ProductResponse(
            productResponse.id,
            productResponse.categoryId,
            productResponse.name,
            productResponse.weight,
            productResponse.stock,
            productResponse.description,
            productResponse.price,
            productResponse.images
        )

        assertTrue(productResponse1 == productResponse2 && productResponse2 == productResponse1)
        assertEquals(productResponse1.hashCode(), productResponse2.hashCode())
    }
}