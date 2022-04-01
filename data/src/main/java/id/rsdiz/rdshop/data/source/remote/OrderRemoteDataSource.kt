package id.rsdiz.rdshop.data.source.remote

import id.rsdiz.rdshop.data.source.remote.mapper.OrderRemoteMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import id.rsdiz.rdshop.data.source.remote.response.order.DetailOrderResponse
import id.rsdiz.rdshop.data.source.remote.response.order.OrderResponse
import id.rsdiz.rdshop.data.model.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRemoteDataSource @Inject constructor(
    val apiService: ApiService,
    val mapper: OrderRemoteMapper
) {
    suspend fun countOrders() =
        flow {
            try {
                val response = apiService.countOrders()
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

    suspend fun getOrders(size: Int = 10) =
        flow {
            try {
                val response = apiService.getOrders(page = 1, size = size, status = null)
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

    suspend fun getOrderById(orderId: String) =
        flow {
            try {
                val response = apiService.getOrderById(
                    orderId = orderId
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

    suspend fun getOrderByUserId(userId: String) =
        flow {
            try {
                val response = apiService.getOrderByUserId(
                    userId = userId
                )
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

    suspend fun getOrderByDate(startDate: OffsetDateTime?, endDate: OffsetDateTime?) =
        flow {
            try {
                val response = if (startDate != null && endDate != null) {
                    apiService.getOrderByDate(
                        startDate = startDate,
                        endDate = endDate
                    )
                } else apiService.getOrderByDate()

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

    suspend fun updateOrder(orderId: String, status: Short, trackingNumber: String = "") =
        flow {
            try {
                val response = apiService.updateOrder(
                    orderId = orderId,
                    status = generateRequestBody(status.toString()),
                    trackingNumber = generateRequestBody(trackingNumber)
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

    suspend fun deleteOrder(orderId: String) =
        flow {
            try {
                val response = apiService.deleteOrder(
                    orderId = orderId
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

    suspend fun createOrder(order: Order) =
        flow {
            try {
                val response = apiService.createOrder(
                    data = generateRequestBody(order)
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

    private fun generateRequestBody(order: Order): OrderResponse =
        OrderResponse(
            id = order.orderId,
            userId = order.userId,
            date = order.date.toString(),
            amount = order.amount,
            shipName = order.shipName,
            shipAddress = order.shipAddress,
            shippingCost = order.shippingCost,
            phone = order.phone,
            status = order.status,
            trackingNumber = order.trackingNumber,
            detailOrder = order.orderDetail.map {
                DetailOrderResponse(
                    id = it.detailId,
                    productId = it.productId,
                    price = it.price,
                    quantity = it.quantity
                )
            }
        )

    private fun generateRequestBody(text: String): RequestBody =
        text.toRequestBody("text/plain".toMediaTypeOrNull())
}
