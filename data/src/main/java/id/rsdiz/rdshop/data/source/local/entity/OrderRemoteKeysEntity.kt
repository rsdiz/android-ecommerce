package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_remote_entity")
data class OrderRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val previous: Int?,
    val next: Int?,
    val lastUpdated: Long?
)
