package id.rsdiz.rdshop.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.base.utils.HeaderInterceptor
import id.rsdiz.rdshop.base.utils.PreferenceHelper
import id.rsdiz.rdshop.base.utils.PreferenceHelper.Ext.get
import id.rsdiz.rdshop.base.utils.SSLCertificateConfigurator
import id.rsdiz.rdshop.data.R
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val trustManagerFactory =
            SSLCertificateConfigurator.getTrustManager(context, R.raw.certificate_rdshop)
        val trustManagers = trustManagerFactory.trustManagers
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException(
                "Unexpected default trust managers:" + Arrays.toString(
                    trustManagers
                )
            )
        }
        val trustManager = trustManagers[0] as X509TrustManager

        return OkHttpClient.Builder()
            .sslSocketFactory(
                SSLCertificateConfigurator.getSSLConfiguration(
                    context,
                    R.raw.certificate_rdshop
                ).socketFactory,
                trustManager
            )
            .addInterceptor(HeaderInterceptor(apiKey = getApiKey(context)))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(Consts.TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Consts.TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideApiService(client: OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl(Consts.RDSHOP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build().create(ApiService::class.java)

    private fun getApiKey(context: Context): String {
        val prefs = PreferenceHelper(context).customPrefs(Consts.PREFERENCE_NAME)

        val apiKey: String = prefs[Consts.PREF_TOKEN, ""]

        return apiKey.ifEmpty {
            Consts.RDSHOP_API_KEY
        }
    }
}
