package com.example.core.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor that blocks requests when there is no internet connection.
 *
 * Throws [NoConnectivityException] (subclass of IOException) before the request
 * is even sent, so Retrofit/Repository catch it as a network error.
 *
 * This interceptor is automatically added to OkHttpClient in [com.example.core.di.CoreNetworkModule].
 */
class NetworkConnectionInterceptor(
    private val networkConnectivity: NetworkConnectivity
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!networkConnectivity.isConnected()) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request().newBuilder().build())
    }
}
