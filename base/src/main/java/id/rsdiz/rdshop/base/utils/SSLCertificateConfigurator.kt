package id.rsdiz.rdshop.base.utils

import android.content.Context
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object SSLCertificateConfigurator {

    @Throws(
        CertificateException::class,
        IOException::class,
        KeyStoreException::class,
        NoSuchAlgorithmException::class,
        KeyManagementException::class
    )

    fun getSSLConfiguration(context: Context, rawCertificate: Int): SSLContext {
        // Creating an SSLSocketFactory that uses our TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, getTrustManager(context, rawCertificate).trustManagers, null)
        return sslContext
    }

    private fun getKeystore(context: Context, rawCertificate: Int): KeyStore {
        // Creating a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", getCertificate(context, rawCertificate))
        return keyStore
    }

    fun getTrustManager(context: Context, rawCertificate: Int): TrustManagerFactory {
        // Creating a TrustManager that trusts the CAs in our KeyStore.
        val trustManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val trustManagerFactory = TrustManagerFactory.getInstance(trustManagerFactoryAlgorithm)
        trustManagerFactory.init(getKeystore(context, rawCertificate))
        return trustManagerFactory
    }

    private fun getCertificate(context: Context, rawCertificate: Int): Certificate? {
        // Loading CAs from file
        val certificateFactory: CertificateFactory? = CertificateFactory.getInstance("X.509")
        return context.resources.openRawResource(rawCertificate)
            .use { cert -> certificateFactory?.generateCertificate(cert) }
    }
}
