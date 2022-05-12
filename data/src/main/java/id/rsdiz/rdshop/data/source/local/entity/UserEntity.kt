package id.rsdiz.rdshop.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val username: String,
    val email: String,
    val name: String,
    val gender: Gender?,
    val address: String?,
    val photo: String?,
    val role: Role
)
