package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val userId: String,
    val date: String,
    val amount: Int,
    val shipName: String,
    val shipAddress: String,
    val shippingCost: Int,
    val phone: String,
    val status: Short,
    val trackingNumber: String
)
