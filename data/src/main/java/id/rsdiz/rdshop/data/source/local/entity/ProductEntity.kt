package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val productId: String,
    val categoryId: String,
    val name: String,
    val weight: Float,
    val stock: Int,
    val description: String,
    val price: Int,
    var isFavorite: Boolean = false
)
