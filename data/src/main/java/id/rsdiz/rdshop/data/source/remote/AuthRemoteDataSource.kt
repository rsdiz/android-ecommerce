package id.rsdiz.rdshop.data.source.remote

import id.rsdiz.rdshop.data.source.remote.mapper.UserRemoteMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSource @Inject constructor(
    val apiService: ApiService,
    val mapper: UserRemoteMapper
) {
    suspend fun signUp(name: String, username: String, email: String, password: String) =
        flow {
            try {
                val response = apiService.signUp(
                    name = generateRequestBody(name),
                    username = generateRequestBody(username),
                    email = generateRequestBody(email),
                    password = generateRequestBody(password)
                )
                when (response.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.data!!
                        )
                    )
                    else -> emit(ApiResponse.Error(response.status))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun signIn(login: String, password: String) =
        flow {
            try {
                val response = apiService.signIn(
                    login = generateRequestBody(login),
                    password = generateRequestBody(password)
                )
                when (response.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.data!!
                        )
                    )
                    else -> emit(ApiResponse.Error(response.status))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun signOut(token: String) =
        flow {
            try {
                val response = apiService.signOut(
                    apiKey = generateRequestBody(token)
                )
                when (response.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.data!!
                        )
                    )
                    else -> emit(ApiResponse.Error(response.status))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    private fun generateRequestBody(text: String): RequestBody =
        text.toRequestBody("text/plain".toMediaTypeOrNull())
}
