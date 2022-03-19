package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_remote_keys")
data class UserRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val previous: Int?,
    val next: Int?,
    val lastUpdated: Long?
)
