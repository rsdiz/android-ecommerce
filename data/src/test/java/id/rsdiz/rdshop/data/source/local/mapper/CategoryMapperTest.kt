package id.rsdiz.rdshop.data.source.local.mapper

import id.rsdiz.rdshop.data.factory.CategoryFactory
import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.data.source.local.entity.CategoryEntity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class CategoryMapperTest {
    private lateinit var categoryMapper: CategoryMapper

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        categoryMapper = CategoryMapper()
    }

    /**
     * Function for testing [CategoryMapper.mapFromEntities] method
     */
    @Test
    fun mapFromEntity() {
        val categoryEntity = CategoryFactory.makeCategoryEntity()
        val category = categoryMapper.mapFromEntity(categoryEntity)

        assertCategoryDataEquals(categoryEntity, category)
    }

    /**
     * Function for testing [CategoryMapper.mapToEntity] method
     */
    @Test
    fun mapToEntity() {
        val category = CategoryFactory.makeCategory()
        val categoryEntity = categoryMapper.mapToEntity(category)

        assertCategoryDataEquals(categoryEntity, category)
    }

    /**
     * Function for testing [CategoryMapper.mapFromEntities] method
     */
    @Test
    fun mapFromEntities() {
        val categoryEntities = CategoryFactory.makeCategoryEntityList()
        val categoryList = categoryMapper.mapFromEntities(categoryEntities)

        repeat(categoryEntities.size) {
            assertCategoryDataEquals(categoryEntities[it], categoryList[it])
        }
    }

    private fun assertCategoryDataEquals(entity: CategoryEntity, category: Category) {
        assertEquals(entity.categoryId, category.categoryId)
        assertEquals(entity.name, category.name)
    }
}
