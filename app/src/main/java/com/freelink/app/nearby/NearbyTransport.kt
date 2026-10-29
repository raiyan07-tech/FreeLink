package com.freelink.app.nearby

import com.freelink.app.domain.models.PeerDevice
import com.freelink.app.domain.models.TransportType

interface DiscoveryListener {
    fun onPeerDiscovered(peer: PeerDevice)
    fun onDiscoveryFailed(errorCode: Int, message: String)
    fun onDiscoveryStopped()
}

interface ConnectionListener {
    fun onConnectionRequested(peer: PeerDevice, onConsentDecision: (Boolean) -> Unit)
    fun onConnected(peerId: String)
    fun onDisconnected(peerId: String)
    fun onPayloadReceived(peerId: String, payload: String)
    fun onError(errorMessage: String)
}

/**
 * Modular transport adapter interface.
 * Can be backed by BLE, Wi-Fi Direct, or Local Simulation.
 */
interface NearbyTransport {
    val transportType: TransportType
    fun isAvailable(): Boolean
    fun startDiscovery(listener: DiscoveryListener)
    fun stopDiscovery()
    fun startAdvertising(localFlId: String, displayName: String)
    fun stopAdvertising()
    fun connectToPeer(peer: PeerDevice, listener: ConnectionListener)
    fun disconnect(peerId: String)
    fun sendPayload(peerId: String, payload: String, callback: (Boolean, String?) -> Unit)
    fun setConnectionListener(listener: ConnectionListener)
}
