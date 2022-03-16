package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DetailAndProduct(
    @Embedded
    val detail: DetailOrderEntity,
    @Relation(
        parentColumn = "detailId",
        entityColumn = "productId",
        associateBy = Junction(DetailProductCrossRef::class)
    )
    val product: ProductEntity
)
