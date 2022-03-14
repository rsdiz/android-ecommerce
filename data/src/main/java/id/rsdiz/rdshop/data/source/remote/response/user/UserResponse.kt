package id.rsdiz.rdshop.data.source.remote.response.user

import com.google.gson.annotations.SerializedName

/**
 * Model response for user
 */
data class UserResponse(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("username")
    val username: String,
    @field:SerializedName("email")
    val email: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("gender")
    val gender: String,
    @field:SerializedName("address")
    val address: String,
    @field:SerializedName("photo")
    val photo: String,
    @field:SerializedName("role")
    val role: String
)
