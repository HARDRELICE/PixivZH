package com.hardrelice.pixivzh

import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

object HttpsUtil {
    /**
     * 信任任何站点，实现https页面的正常访问
     *
     */
    fun trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
            val context = SSLContext.getInstance("TLS")
            context.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }
            }), SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }
    fun disableSSLCertificateChecking() {
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                return null
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(arg0: Array<X509Certificate>, arg1: String) {
                // Not implemented
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(arg0: Array<X509Certificate>, arg1: String) {
                // Not implemented
            }
        })
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }
}