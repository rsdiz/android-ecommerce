package id.rsdiz.rdshop.data.source.remote

import id.rsdiz.rdshop.data.source.remote.mapper.CategoryRemoteMapper
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
class CategoryRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    val mapper: CategoryRemoteMapper
) {
    suspend fun countCategories() =
        flow {
            try {
                val response = apiService.countCategories()
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

    suspend fun getCategories() =
        flow {
            try {
                val response = apiService.getCategories()
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

    suspend fun updateCategory(categoryId: String, newName: String) =
        flow {
            try {
                val response = apiService.updateCategory(
                    categoryId = categoryId,
                    name = generateRequestBody(newName)
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

    suspend fun deleteCategory(categoryId: String) =
        flow {
            try {
                val response = apiService.deleteCategory(
                    categoryId = categoryId
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

    suspend fun createCategory(name: String) =
        flow {
            try {
                val response = apiService.createCategory(
                    name = generateRequestBody(name)
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
