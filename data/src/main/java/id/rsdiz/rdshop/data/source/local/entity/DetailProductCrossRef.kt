package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity

@Entity(primaryKeys = ["detailId", "productId"])
data class DetailProductCrossRef(
    val detailId: String,
    val productId: String
)
