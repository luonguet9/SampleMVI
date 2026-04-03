package com.example.core.di

import android.content.Context
import com.example.core.network.NetworkConnectionInterceptor
import com.example.core.network.NetworkConnectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Core Hilt module providing generic network infrastructure.
 *
 * Provides: [NetworkConnectivity], [OkHttpClient], [Retrofit]
 * Does NOT provide any feature-specific API services (e.g., UserApi).
 * Those are provided in the feature module's own DI module.
 *
 * HOW TO USE IN NEW PROJECT:
 * 1. Set the base URL by overriding [provideRetrofit] or via a BuildConfig constant
 * 2. Add your API service in your app's NetworkModule:
 *    ```kotlin
 *    @Module @InstallIn(SingletonComponent::class)
 *    object AppNetworkModule {
 *        @Singleton @Provides
 *        fun provideMyApi(retrofit: Retrofit): MyApi = retrofit.create(MyApi::class.java)
 *    }
 *    ```
 *
 * NOTE: [BASE_URL] is intentionally left as a placeholder here.
 * Override or replace it per-project via a companion constant or BuildConfig field.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreNetworkModule {

    /**
     * Override this constant in your app module or pass via BuildConfig.
     * Example: `buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")`
     */
    private const val BASE_URL = "https://69c8c3ad68edf52c954df8a0.mockapi.io/"
    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L

    @Singleton
    @Provides
    fun provideNetworkConnectivity(@ApplicationContext context: Context): NetworkConnectivity =
        NetworkConnectivity(context)

    @Singleton
    @Provides
    fun provideOkHttpClient(networkConnectivity: NetworkConnectivity): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(NetworkConnectionInterceptor(networkConnectivity))
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
