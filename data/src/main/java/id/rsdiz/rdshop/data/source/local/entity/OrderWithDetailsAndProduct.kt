package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithDetailsAndProduct(
    @Embedded
    val order: OrderEntity,
    @Relation(
        entity = DetailOrderEntity::class,
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val details: List<DetailAndProduct>
)
