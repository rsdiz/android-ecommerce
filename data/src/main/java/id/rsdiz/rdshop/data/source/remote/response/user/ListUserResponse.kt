package id.rsdiz.rdshop.data.source.remote.response.user

import com.google.gson.annotations.SerializedName

/**
 * Model response for users
 */
data class ListUserResponse(
    @field:SerializedName("count")
    val count: Int,
    @field:SerializedName("next")
    val next: String?,
    @field:SerializedName("previous")
    val previous: String?,
    @field:SerializedName("results")
    val results: List<UserResponse>
)
