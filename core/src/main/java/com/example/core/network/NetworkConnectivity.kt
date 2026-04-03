package com.example.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.IOException

/**
 * Utility class for monitoring network connectivity.
 *
 * Provides both synchronous check [isConnected] and reactive [observeConnectivity] Flow.
 *
 * HOW TO USE IN NEW PROJECT:
 * Provided via Hilt in [com.example.core.di.CoreNetworkModule].
 * Inject where needed: `@Inject constructor(private val network: NetworkConnectivity)`
 */
class NetworkConnectivity(
    private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /** Returns true if the device currently has an active, validated internet connection. */
    fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Emits [NetworkStatus] changes reactively.
     * Emits the current status immediately upon collection.
     */
    fun observeConnectivity(): Flow<NetworkStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Available)
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.Lost)
            }

            override fun onUnavailable() {
                trySend(NetworkStatus.Unavailable)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val isWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                val isCellular =
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

                when {
                    isWifi     -> trySend(NetworkStatus.AvailableWifi)
                    isCellular -> trySend(NetworkStatus.AvailableCellular)
                    else       -> trySend(NetworkStatus.Available)
                }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Emit initial state immediately
        trySend(if (isConnected()) NetworkStatus.Available else NetworkStatus.Unavailable)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    fun getNetworkType(): NetworkType {
        val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return NetworkType.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            else -> NetworkType.UNKNOWN
        }
    }
}

sealed class NetworkStatus {
    object Available         : NetworkStatus()
    object AvailableWifi     : NetworkStatus()
    object AvailableCellular : NetworkStatus()
    object Lost              : NetworkStatus()
    object Unavailable       : NetworkStatus()

    fun isConnected(): Boolean = this is Available || this is AvailableWifi || this is AvailableCellular
}

enum class NetworkType { WIFI, CELLULAR, ETHERNET, UNKNOWN, NONE }

/** Thrown by [NetworkConnectionInterceptor] when there is no internet connection. */
class NoConnectivityException : IOException("No Internet Connection")
