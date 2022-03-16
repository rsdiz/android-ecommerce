package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithOrders(
    @Embedded
    val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val orders: List<OrderEntity>
)
