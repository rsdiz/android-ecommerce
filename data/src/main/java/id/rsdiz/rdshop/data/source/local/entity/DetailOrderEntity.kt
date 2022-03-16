package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detail_orders")
data class DetailOrderEntity(
    @PrimaryKey
    val detailId: String,
    val orderId: String,
    val productId: String,
    val price: Int,
    val quantity: Int
)
