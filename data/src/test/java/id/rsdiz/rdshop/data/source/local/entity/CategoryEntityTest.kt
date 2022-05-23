package id.rsdiz.rdshop.data.source.local.entity

import id.rsdiz.rdshop.data.factory.CategoryFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class CategoryEntityTest {
    private lateinit var categoryEntity: CategoryEntity

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        categoryEntity = CategoryFactory.makeCategoryEntity()
    }

    /**
     * Test an Equation for object symmetric
     */
    @Test
    fun testEqualsSymmetric() {
        val categoryEntity1 = CategoryEntity(
            categoryEntity.categoryId,
            categoryEntity.name
        )

        val categoryEntity2 = CategoryEntity(
            categoryEntity.categoryId,
            categoryEntity.name
        )

        assertTrue(categoryEntity1 == categoryEntity2 && categoryEntity2 == categoryEntity1)
        assertEquals(categoryEntity1.hashCode(), categoryEntity2.hashCode())
    }
}
