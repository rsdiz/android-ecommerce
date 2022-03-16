package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithImages(
    @Embedded
    val product: ProductEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val images: List<ProductImageEntity>
)
