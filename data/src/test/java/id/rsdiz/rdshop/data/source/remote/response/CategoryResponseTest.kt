package id.rsdiz.rdshop.data.source.remote.response

import id.rsdiz.rdshop.data.factory.CategoryFactory
import id.rsdiz.rdshop.data.source.remote.response.category.CategoryResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class CategoryResponseTest {
    private lateinit var categoryResponse: CategoryResponse

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        categoryResponse = CategoryFactory.makeCategoryResponse()
    }

    /**
     * Test an Equation for object symmetric
     */
    @Test
    fun testEqualsSymmetric() {
        val categoryResponse1 = CategoryResponse(
            categoryResponse.id,
            categoryResponse.name
        )

        val categoryResponse2 = CategoryResponse(
            categoryResponse.id,
            categoryResponse.name
        )

        assertTrue(categoryResponse1 == categoryResponse2 && categoryResponse2 == categoryResponse1)
        assertEquals(categoryResponse1.hashCode(), categoryResponse2.hashCode())
    }
}
