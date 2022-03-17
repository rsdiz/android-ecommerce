package id.rsdiz.rdshop.data.source.remote.mapper

import id.rsdiz.rdshop.data.source.local.entity.CategoryEntity
import id.rsdiz.rdshop.data.source.remote.mapper.base.EntityMapper
import id.rsdiz.rdshop.data.source.remote.response.category.CategoryResponse
import javax.inject.Inject

/**
 * Maps a Category from remote (API) to our model
 */
open class CategoryRemoteMapper @Inject constructor() :
    EntityMapper<CategoryResponse, CategoryEntity> {
    override fun mapRemoteToEntity(remote: CategoryResponse): CategoryEntity =
        CategoryEntity(
            categoryId = remote.id,
            name = remote.name
        )

    override fun mapRemoteToEntities(remotes: List<CategoryResponse>): List<CategoryEntity> {
        val list = mutableListOf<CategoryEntity>()

        remotes.map {
            list.add(mapRemoteToEntity(it))
        }

        return list
    }
}
