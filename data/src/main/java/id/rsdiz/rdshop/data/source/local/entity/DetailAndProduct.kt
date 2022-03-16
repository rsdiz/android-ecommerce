package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DetailAndProduct(
    @Embedded
    val detail: DetailOrderEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val product: ProductEntity
)
