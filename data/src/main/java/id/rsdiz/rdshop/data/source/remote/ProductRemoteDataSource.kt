package id.rsdiz.rdshop.data.source.remote

import id.rsdiz.rdshop.base.utils.FileHelper
import id.rsdiz.rdshop.data.source.remote.mapper.ProductRemoteMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import id.rsdiz.rdshop.data.model.Product
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
class ProductRemoteDataSource @Inject constructor(
    val apiService: ApiService,
    val mapper: ProductRemoteMapper,
) {
    suspend fun countProducts(type: String?) =
        flow {
            try {
                val response = apiService.countProducts(type)
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

    suspend fun getProducts(size: Int = 10) =
        flow {
            try {
                val response = apiService.getProducts(size = size)
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

    suspend fun getProductById(productId: String) =
        flow {
            try {
                val response = apiService.getProductById(
                    productId = productId
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

    suspend fun updateProduct(product: Product, sourceFile: File?) =
        flow {
            try {
                val response = apiService.updateProduct(
                    productId = product.productId,
                    categoryId = generateRequestBody(product.categoryId),
                    name = generateRequestBody(product.name),
                    weight = generateRequestBody(product.weight.toString()),
                    stock = generateRequestBody(product.stock.toString()),
                    description = generateRequestBody(product.description),
                    price = generateRequestBody(product.price.toString()),
                    image = generateRequestMultipart(sourceFile)
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

    suspend fun deleteProduct(productId: String) =
        flow {
            try {
                val response = apiService.deleteProduct(
                    productId = productId
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

    suspend fun createProduct(product: Product, sourceFile: File?) =
        flow {
            try {
                val response = apiService.createProduct(
                    productId = generateRequestBody(product.productId),
                    categoryId = generateRequestBody(product.categoryId),
                    name = generateRequestBody(product.name),
                    weight = generateRequestBody(product.weight.toString()),
                    stock = generateRequestBody(product.stock.toString()),
                    description = generateRequestBody(product.description),
                    price = generateRequestBody(product.price.toString()),
                    image = generateRequestMultipart(sourceFile)
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

    suspend fun deleteImage(productId: String, imageId: String) =
        flow {
            try {
                val response = apiService.deleteProductImage(
                    productId = productId,
                    imageId = imageId
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

    suspend fun addImage(productId: String, sourceFile: File) =
        flow {
            try {
                val response = apiService.addProductImage(
                    productId = productId,
                    image = generateRequestMultipart(sourceFile)!!
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

    private fun generateRequestBody(product: Product, sourceFile: File?): RequestBody {
        // Set text data
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", product.name)
            .addFormDataPart("weight", product.weight.toString())
            .addFormDataPart("stock", product.stock.toString())
            .addFormDataPart("description", product.description)
            .addFormDataPart("price", product.price.toString())

        // Set file data if available
        sourceFile?.let { file ->
            val mimeType = FileHelper.getMimeType(file)
            mimeType?.let {
                multipartBody.addFormDataPart(
                    "image",
                    file.nameWithoutExtension,
                    file.asRequestBody(it.toMediaTypeOrNull())
                )
            }
        }

        return multipartBody.build()
    }

    private fun generateRequestBody(sourceFile: File): RequestBody {
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        val mimeType = FileHelper.getMimeType(sourceFile)
        mimeType?.let {
            multipartBody.addFormDataPart(
                "photoFile",
                sourceFile.nameWithoutExtension,
                sourceFile.asRequestBody(it.toMediaTypeOrNull())
            )
        }
        return multipartBody.build()
    }

    private fun generateRequestBody(text: String, type: String = "text/plain"): RequestBody =
        text.toRequestBody(type.toMediaTypeOrNull())

    private fun generateRequestMultipart(sourceFile: File?): MultipartBody.Part? {
        val filePart = sourceFile?.let {
            MultipartBody.Part.createFormData(
                "image",
                it.nameWithoutExtension,
                it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        return filePart
    }
}
