package id.rsdiz.rdshop.data.source.remote

import id.rsdiz.rdshop.base.utils.FileHelper
import id.rsdiz.rdshop.data.source.remote.mapper.UserRemoteMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import id.rsdiz.rdshop.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    val mapper: UserRemoteMapper
) {
    suspend fun getUsers() =
        flow {
            try {
                val response = apiService.getUsers()
                when (response.code) {
                    200 -> if (response.data.count > 0) {
                        emit(
                            ApiResponse.Success(
                                data = response.data
                            )
                        )
                    } else {
                        emit(ApiResponse.Empty)
                    }
                    else -> emit(ApiResponse.Error(response.status))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getUserById(userId: String) =
        flow {
            try {
                val response = apiService.getUserById(
                    userId = userId
                )
                when (response.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.data
                        )
                    )
                    else -> emit(ApiResponse.Error(response.status))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun updateUser(user: User, password: String, sourceFile: File?) =
        flow {
            try {
                val response = apiService.updateUser(
                    userId = user.userId,
                    data = generateRequestBody(user, password, sourceFile)
                )
                when (response.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.data
                        )
                    )
                    else -> emit(ApiResponse.Error(response.status))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun updatePassword(userId: String, oldPassword: String, newPassword: String) =
        flow {
            try {
                val response = apiService.updateUserPassword(
                    userId = userId,
                    oldPassword = generateRequestBody(oldPassword),
                    newPassword = generateRequestBody(newPassword)
                )
                when (response.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.data
                        )
                    )
                    else -> emit(ApiResponse.Error(response.status))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun deleteUser(userId: String) =
        flow {
            try {
                val response = apiService.deleteUser(
                    userId = userId
                )
                when (response.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.data
                        )
                    )
                    else -> emit(ApiResponse.Error(response.status))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun createUser(user: User, password: String, sourceFile: File?) =
        flow {
            try {
                val response = apiService.createUser(
                    data = generateRequestBody(user, password, sourceFile)
                )
                when (response.code) {
                    200 -> emit(
                        ApiResponse.Success(
                            data = response.data
                        )
                    )
                    else -> emit(ApiResponse.Error(response.status))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    private fun generateRequestBody(user: User, password: String, sourceFile: File?): RequestBody {
        // Set text data
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", user.name)
            .addFormDataPart("email", user.email)
            .addFormDataPart("username", user.username)
            .addFormDataPart("password", password)
            .addFormDataPart("gender", user.gender.toString())
            .addFormDataPart("address", user.address)
            .addFormDataPart("role", user.role.toString())

        // Set file data if available
        sourceFile?.let { file ->
            val mimeType = FileHelper.getMimeType(file)
            mimeType?.let {
                multipartBody.addFormDataPart(
                    "photoFile",
                    file.nameWithoutExtension,
                    file.asRequestBody(it.toMediaTypeOrNull())
                )
            }
        }

        return multipartBody.build()
    }

    private fun generateRequestBody(text: String): RequestBody =
        text.toRequestBody("text/plain".toMediaTypeOrNull())
}
