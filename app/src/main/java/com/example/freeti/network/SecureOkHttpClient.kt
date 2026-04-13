package com.example.freeti.network

import android.content.Context
import com.example.freeti.R
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object SecureOkHttpClient {

    fun create(context: Context): OkHttpClient {

        val certificateFactory = CertificateFactory.getInstance("X.509")
        val inputStream = context.resources.openRawResource(R.raw.server_certificate)
        val certificate = certificateFactory.generateCertificate(inputStream)
        inputStream.close()

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("server", certificate)

        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(keyStore)

        val trustManagers = trustManagerFactory.trustManagers
        val x509TrustManager = trustManagers[0] as X509TrustManager

        // 4. Создаем SSLContext
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(x509TrustManager), null)

        // 5. Собираем OkHttpClient
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, x509TrustManager)
            .addInterceptor(LoggingInterceptor()) // для отладки
            .build()
    }
}