package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_images")
data class ProductImageEntity(
    @PrimaryKey
    val imageId: String,
    val productId: String,
    val path: String
)
