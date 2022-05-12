package id.rsdiz.rdshop.base.utils

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Class to invoke the Api key to header
 */
class HeaderInterceptor(
    private val headerName: String,
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().addHeader(headerName, apiKey).build()
        return chain.proceed(request)
    }
}
