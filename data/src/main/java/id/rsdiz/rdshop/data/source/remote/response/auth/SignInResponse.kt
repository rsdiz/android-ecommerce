package id.rsdiz.rdshop.data.source.remote.response.auth

import com.google.gson.annotations.SerializedName
import id.rsdiz.rdshop.data.source.remote.response.IBaseResponse
import id.rsdiz.rdshop.data.source.remote.response.user.UserResponse

/**
 * Model response for Sign In
 */
data class SignInResponse(
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val user: UserResponse,
    @field:SerializedName("token")
    val apiKey: String,
)

/**
 * Sign In Responses from API
 */
data class BaseSignInResponse(
    override val code: Int,
    override val status: String,
    override val data: SignInResponse?
) : IBaseResponse<SignInResponse>
