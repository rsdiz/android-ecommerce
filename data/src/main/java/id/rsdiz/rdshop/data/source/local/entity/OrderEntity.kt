package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val orderId: String,
    val userId: String,
    val date: OffsetDateTime,
    val amount: Int,
    val shipName: String,
    val shipAddress: String,
    val shippingCost: Int,
    val phone: String,
    val status: Short,
    val trackingNumber: String?
)
