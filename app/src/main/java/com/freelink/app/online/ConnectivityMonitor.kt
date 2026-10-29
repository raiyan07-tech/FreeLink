package com.freelink.app.online

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.freelink.app.domain.models.NetworkState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Monitors real Android network connectivity without guessing the billing status.
 */
class ConnectivityMonitor(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _networkState = MutableStateFlow(getCurrentState())
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    init {
        registerCallback()
    }

    private fun getCurrentState(): NetworkState {
        val cm = connectivityManager ?: return NetworkState.OFFLINE
        val activeNetwork = cm.activeNetwork ?: return NetworkState.OFFLINE
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return NetworkState.OFFLINE

        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return if (hasInternet && isValidated) {
            NetworkState.INTERNET_AVAILABLE
        } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            NetworkState.CELLULAR_NO_DATA
        } else {
            NetworkState.OFFLINE
        }
    }

    private fun registerCallback() {
        val cm = connectivityManager ?: return
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _networkState.value = getCurrentState()
            }

            override fun onLost(network: Network) {
                _networkState.value = getCurrentState()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                _networkState.value = getCurrentState()
            }
        })
    }
}
