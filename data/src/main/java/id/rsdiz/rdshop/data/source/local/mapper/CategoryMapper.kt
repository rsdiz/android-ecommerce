package id.rsdiz.rdshop.data.source.local.mapper

import id.rsdiz.rdshop.data.source.local.entity.CategoryEntity
import id.rsdiz.rdshop.data.source.local.mapper.base.DataMapper
import id.rsdiz.rdshop.data.model.Category
import javax.inject.Inject

/**
 * Mapper for category entity from data to domain layer
 */
open class CategoryMapper @Inject constructor() : DataMapper<CategoryEntity, Category> {
    override fun mapFromEntity(entity: CategoryEntity): Category =
        Category(
            categoryId = entity.categoryId,
            name = entity.name
        )

    override fun mapFromEntities(entities: List<CategoryEntity>): List<Category> =
        entities.map {
            mapFromEntity(it)
        }

    override fun mapToEntity(model: Category): CategoryEntity =
        CategoryEntity(
            categoryId = model.categoryId,
            name = model.name
        )
}
