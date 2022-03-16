package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithProducts(
    @Embedded
    val category: CategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val products: List<ProductEntity>
)
