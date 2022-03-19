package id.rsdiz.rdshop.data.source.remote.network

import id.rsdiz.rdshop.data.source.remote.response.BaseStringResponse
import id.rsdiz.rdshop.data.source.remote.response.category.BaseCategoriesResponse
import id.rsdiz.rdshop.data.source.remote.response.category.BaseCategoryResponse
import id.rsdiz.rdshop.data.source.remote.response.order.BaseOrderResponse
import id.rsdiz.rdshop.data.source.remote.response.order.BaseOrdersResponse
import id.rsdiz.rdshop.data.source.remote.response.order.OrderResponse
import id.rsdiz.rdshop.data.source.remote.response.product.*
import id.rsdiz.rdshop.data.source.remote.response.user.BaseUserResponse
import id.rsdiz.rdshop.data.source.remote.response.user.BaseUsersResponse
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Contract for available services from API Server
 */
interface ApiService {
    @GET(value = "users")
    suspend fun getUsers(@Query("page") page: Int = 1, @Query("size") size: Int = 20): BaseUsersResponse

    @GET(value = "products")
    suspend fun getProducts(@Query("size") size: Int = 10): BaseProductsResponse

    @GET(value = "categories")
    suspend fun getCategories(): BaseCategoriesResponse

    @GET(value = "orders")
    suspend fun getOrders(@Query("size") size: Int = 10): BaseOrdersResponse

    @GET(value = "users/{userId}")
    suspend fun getUserById(@Path(value = "userId") userId: String): BaseUserResponse

    @GET(value = "products/{productId}")
    suspend fun getProductById(@Path(value = "productId") productId: String): BaseProductResponse

    @GET(value = "orders/{orderId}")
    suspend fun getOrderById(@Path(value = "orderId") orderId: String): BaseOrderResponse

    @Multipart
    @PUT(value = "users/{userId}")
    suspend fun updateUser(
        @Path(value = "userId") userId: String,
        @Body data: RequestBody
    ): BaseUserResponse

    @Multipart
    @PUT(value = "users/password/{userId}")
    suspend fun updateUserPassword(
        @Path(value = "userId") userId: String,
        @Part(value = "old_password") oldPassword: RequestBody,
        @Part(value = "new_password") newPassword: RequestBody
    ): BaseUserResponse

    @Multipart
    @PUT(value = "categories/{categoryId}")
    suspend fun updateCategory(
        @Path(value = "categoryId") categoryId: String,
        @Part(value = "name") name: RequestBody
    ): BaseCategoryResponse

    @Multipart
    @PUT(value = "products/{productId}")
    suspend fun updateProduct(
        @Path(value = "productId") productId: String,
        @Body data: RequestBody
    ): BaseProductResponse

    @Multipart
    @PUT(value = "orders/{orderId}")
    suspend fun updateOrder(
        @Path(value = "orderId") orderId: String,
        @Part(value = "status") status: RequestBody,
        @Part(value = "tracking_number") trackingNumber: RequestBody?
    ): BaseOrderResponse

    @DELETE(value = "users/{userId}")
    suspend fun deleteUser(@Path(value = "userId") userId: String): BaseStringResponse

    @DELETE(value = "categories/{categoryId}")
    suspend fun deleteCategory(@Path(value = "categoryId") categoryId: String): BaseStringResponse

    @DELETE(value = "products/{productId}")
    suspend fun deleteProduct(@Path(value = "productId") productId: String): BaseStringResponse

    @DELETE(value = "products/{productId}/{imageId}")
    suspend fun deleteProductImage(
        @Path(value = "productId") productId: String,
        @Path(value = "imageId") imageId: String
    ): BaseStringResponse

    @DELETE(value = "orders/{orderId}")
    suspend fun deleteOrder(@Path(value = "orderId") orderId: String): BaseStringResponse

    @Multipart
    @POST(value = "users")
    suspend fun createUser(@Body data: RequestBody): BaseUserResponse

    @Multipart
    @POST(value = "categories")
    suspend fun createCategory(@Part(value = "name") name: RequestBody): BaseCategoryResponse

    @Multipart
    @POST(value = "products")
    suspend fun createProduct(@Body data: RequestBody): BaseProductResponse

    @Multipart
    @POST(value = "products/{productId}")
    suspend fun addProductImage(
        @Path(value = "productId") productId: String,
        @Part(value = "image") image: RequestBody
    ): BaseProductImageResponse

    @POST(value = "orders")
    suspend fun createOrder(@Body data: OrderResponse): BaseOrderResponse
}
