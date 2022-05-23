package id.rsdiz.rdshop.data.source.remote.mapper

import id.rsdiz.rdshop.data.factory.CategoryFactory
import id.rsdiz.rdshop.data.source.local.entity.CategoryEntity
import id.rsdiz.rdshop.data.source.remote.response.category.CategoryResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class CategoryRemoteMapperTest {
    private lateinit var categoryRemoteMapper: CategoryRemoteMapper

    /**
     * Run before start testing
     */
    @Before
    fun setUp() {
        categoryRemoteMapper = CategoryRemoteMapper()
    }

    /**
     * Function for testing [CategoryRemoteMapper.mapRemoteToEntity] method
     */
    @Test
    fun mapRemoteToEntity() {
        val categoryRemote = CategoryFactory.makeCategoryResponse()
        val categoryEntity = categoryRemoteMapper.mapRemoteToEntity(categoryRemote)

        assertCategoryDataEquals(categoryEntity, categoryRemote)
    }

    /**
     * Function for testing [CategoryRemoteMapper.mapRemoteToEntities] method
     */
    @Test
    fun mapRemoteToEntities() {
        val categoryRemoteList = CategoryFactory.makeCategoryResponseList()
        val categoryEntities = categoryRemoteMapper.mapRemoteToEntities(categoryRemoteList)

        repeat(categoryRemoteList.size) {
            assertCategoryDataEquals(categoryEntities[it], categoryRemoteList[it])
        }
    }

    private fun assertCategoryDataEquals(entity: CategoryEntity, response: CategoryResponse) {
        assertEquals(entity.categoryId, response.id)
        assertEquals(entity.name, response.name)
    }
}
