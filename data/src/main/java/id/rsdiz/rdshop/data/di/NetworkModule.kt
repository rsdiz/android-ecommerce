package id.rsdiz.rdshop.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.rsdiz.rdshop.base.utils.*
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.data.R
import id.rsdiz.rdshop.data.source.remote.network.ApiRajaOngkirService
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideApiService(@ApplicationContext context: Context): ApiService {
        val trustManagerFactory =
            SSLCertificateConfigurator.getTrustManager(context, R.raw.certificate_rdshop2)
        val trustManagers = trustManagerFactory.trustManagers
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException(
                "Unexpected default trust managers:" + Arrays.toString(
                    trustManagers
                )
            )
        }
        val trustManager = trustManagers[0] as X509TrustManager

        val client = OkHttpClient.Builder()
            .sslSocketFactory(
                SSLCertificateConfigurator.getSSLConfiguration(
                    context,
                    R.raw.certificate_rdshop2
                ).socketFactory,
                trustManager
            )
            .addInterceptor(HeaderInterceptor(headerName = Consts.API_HEADER_KEY, apiKey = getApiKey(context)))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(Consts.TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Consts.TIMEOUT, TimeUnit.SECONDS)

        return Retrofit.Builder()
            .baseUrl(Consts.RDSHOP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build().create(ApiService::class.java)
    }

    private fun getApiKey(context: Context): String {
        val prefs = PreferenceHelper(context).customPrefs(Consts.PREFERENCE_NAME)

        val apiKey: String = prefs[Consts.PREF_TOKEN, ""]

        return apiKey.ifEmpty {
            Consts.RDSHOP_API_KEY
        }
    }

    @Provides
    fun provideApiRajaOngkirService(@ApplicationContext context: Context): ApiRajaOngkirService {
        val httpCacheDir = File(context.cacheDir, "rajaongkir-cache")
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(httpCacheDir, cacheSize.toLong())

        val client = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor(headerName = Consts.RAJAONGKIR_API_HEADER_KEY, apiKey = Consts.RAJAONGKIR_API_KEY))
            .addInterceptor(CacheInterceptor(maxAge = 5))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(Consts.TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Consts.TIMEOUT, TimeUnit.SECONDS)
            .cache(cache)

        return Retrofit.Builder()
            .baseUrl(Consts.RAJAONGKIR_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build().create(ApiRajaOngkirService::class.java)
    }
}
