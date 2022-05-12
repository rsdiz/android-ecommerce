package id.rsdiz.rdshop.data.source.remote

import id.rsdiz.rdshop.base.utils.FileHelper
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.data.source.remote.mapper.UserRemoteMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import id.rsdiz.rdshop.data.source.remote.network.ApiService
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
    val apiService: ApiService,
    val mapper: UserRemoteMapper
) {
    suspend fun countUsers() =
        flow {
            try {
                val response = apiService.countUsers()
                if (response.data != null) {
                    when (response.code) {
                        200 -> emit(
                            ApiResponse.Success(
                                data = response.data
                            )
                        )
                        else -> emit(ApiResponse.Error(response.status))
                    }
                } else emit(ApiResponse.Empty)
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.localizedMessage ?: e.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getUsers(size: Int = 10) =
        flow {
            try {
                val response = apiService.getUsers(size = size)
                if (response.data != null) {
                    when (response.code) {
                        200 -> emit(
                            ApiResponse.Success(
                                data = response.data
                            )
                        )
                        else -> emit(ApiResponse.Error(response.status))
                    }
                } else emit(ApiResponse.Empty)
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
                            data = response.data!!
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
                    name = generateRequestBody(user.name),
                    email = generateRequestBody(user.email),
                    username = generateRequestBody(user.username),
                    password = generateRequestBody(password),
                    gender = generateRequestBody(user.gender.toString()),
                    address = generateRequestBody(user.address.toString()),
                    role = generateRequestBody(user.role.toString()),
                    photoFile = generateRequestMultipart(sourceFile)
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
                            data = response.status
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
            .addFormDataPart("address", user.address.toString())
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

    private fun generateRequestBody(text: String, type: String = "text/plain"): RequestBody =
        text.toRequestBody(type.toMediaTypeOrNull())

    private fun generateRequestBody(file: File?, type: String = "image/*"): RequestBody? =
        file?.asRequestBody(type.toMediaTypeOrNull())

    private fun generateRequestMultipart(sourceFile: File?): MultipartBody.Part? {
        val filePart = sourceFile?.let {
            MultipartBody.Part.createFormData(
                "photoFile",
                it.nameWithoutExtension,
                it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        return filePart
    }
}
