package id.rsdiz.rdshop.data.factory

import id.rsdiz.rdshop.data.model.Category
import id.rsdiz.rdshop.data.source.local.entity.CategoryEntity
import id.rsdiz.rdshop.data.source.remote.response.category.CategoryResponse

/**
 * This class is used to generate [Category], [CategoryEntity], and [CategoryResponse] for usage on tests
 */
object CategoryFactory {
    /**
     * Return a [Category]
     */
    fun makeCategory(): Category =
        Category(
            categoryId = DataFactory.randomString(),
            name = DataFactory.randomString()
        )

    /**
     * Return an list of [Category]
     */
    fun makeCategories(count: Int = 5): List<Category> {
        val list = mutableListOf<Category>()
        repeat(count) {
            list.add(makeCategory())
        }
        return list
    }

    /**
     * Return a [CategoryEntity]
     */
    fun makeCategoryEntity(): CategoryEntity =
        CategoryEntity(
            categoryId = DataFactory.randomString(),
            name = DataFactory.randomString()
        )

    /**
     * Return an list of [CategoryEntity]
     */
    fun makeCategoryEntityList(count: Int = 5): List<CategoryEntity> {
        val list = mutableListOf<CategoryEntity>()
        repeat(count) {
            list.add(makeCategoryEntity())
        }
        return list
    }

    /**
     * Return a [CategoryResponse]
     */
    fun makeCategoryResponse(): CategoryResponse =
        CategoryResponse(
            id = DataFactory.randomString(),
            name = DataFactory.randomString()
        )

    /**
     * Return an list of [CategoryResponse]
     */
    fun makeCategoryResponseList(count: Int = 5): List<CategoryResponse> {
        val list = mutableListOf<CategoryResponse>()
        repeat(count) {
            list.add(makeCategoryResponse())
        }
        return list
    }
}
