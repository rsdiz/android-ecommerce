package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_remote_keys")
data class ProductRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val previous: Int?,
    val next: Int?,
    val lastUpdated: Long?
)
