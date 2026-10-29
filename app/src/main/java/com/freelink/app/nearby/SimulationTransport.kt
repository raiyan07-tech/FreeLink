package com.freelink.app.nearby

import com.freelink.app.domain.models.PeerDevice
import com.freelink.app.domain.models.TransportType
import com.freelink.app.messaging.MessageEnvelope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Clearly labelled Developer Demo / Local Simulation Transport.
 * Used for testing messaging state machines, consent UI, and delivery receipts
 * when physical Bluetooth radios or secondary test devices are not present.
 */
class SimulationTransport : NearbyTransport {

    override val transportType: TransportType = TransportType.LOCAL_DEMO_SIMULATION
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private var discoveryListener: DiscoveryListener? = null
    private var connectionListener: ConnectionListener? = null
    private val connectedPeers = ConcurrentHashMap<String, PeerDevice>()

    // Simulated nearby devices for demo / testing
    private val simulatedPeers = listOf(
        PeerDevice(
            id = "SIM-DEVICE-01",
            flId = "8941203921",
            displayName = "Demo Peer: Rahul (Station Lounge)",
            rssi = -64,
            transportType = TransportType.LOCAL_DEMO_SIMULATION,
            publicKeyFingerprint = "7F3A B92C 44E1 89D0"
        ),
        PeerDevice(
            id = "SIM-DEVICE-02",
            flId = "9182374612",
            displayName = "Demo Peer: Station Volunteer",
            rssi = -78,
            transportType = TransportType.LOCAL_DEMO_SIMULATION,
            publicKeyFingerprint = "5D21 8E3B F49A 12C3"
        )
    )

    private var isScanning = false
    private var scanJob: Job? = null

    override fun isAvailable(): Boolean = true

    override fun startDiscovery(listener: DiscoveryListener) {
        this.discoveryListener = listener
        isScanning = true
        scanJob?.cancel()
        scanJob = scope.launch {
            delay(800) // Realistic discovery latency
            simulatedPeers.forEach { peer ->
                if (isScanning) {
                    listener.onPeerDiscovered(peer)
                    delay(500)
                }
            }
        }
    }

    override fun stopDiscovery() {
        isScanning = false
        scanJob?.cancel()
        discoveryListener?.onDiscoveryStopped()
    }

    override fun startAdvertising(localFlId: String, displayName: String) {
        // In simulation mode, advertising is always active
    }

    override fun stopAdvertising() {
        // No-op for simulation
    }

    override fun connectToPeer(peer: PeerDevice, listener: ConnectionListener) {
        this.connectionListener = listener
        scope.launch {
            delay(600) // Connection negotiation
            connectedPeers[peer.id] = peer
            listener.onConnected(peer.id)
        }
    }

    override fun disconnect(peerId: String) {
        connectedPeers.remove(peerId)
        connectionListener?.onDisconnected(peerId)
    }

    override fun sendPayload(peerId: String, payload: String, callback: (Boolean, String?) -> Unit) {
        if (!connectedPeers.containsKey(peerId)) {
            callback(false, "Simulation peer $peerId not connected")
            return
        }

        // Simulate sending over radio
        scope.launch {
            delay(400) // Transmission latency
            callback(true, null)

            // Parse envelope to simulate peer ACK and optional reply
            val envelope = MessageEnvelope.fromJsonString(payload)
            if (envelope != null && envelope.contentType != "ack") {
                delay(800)
                // Send back protocol ACK
                val ackEnvelope = MessageEnvelope.createAckEnvelope(
                    originalMessageId = envelope.messageId,
                    senderFlId = envelope.recipientFlId,
                    recipientFlId = envelope.senderFlId
                )
                connectionListener?.onPayloadReceived(peerId, ackEnvelope.toJsonString())

                // Optionally simulate peer response
                delay(1500)
                val replyPayload = MessageEnvelope(
                    senderFlId = envelope.recipientFlId,
                    recipientFlId = envelope.senderFlId,
                    contentType = "text/plain",
                    payloadLength = 38,
                    ciphertextBase64 = "I received your message offline safely!",
                    ivBase64 = ""
                )
                connectionListener?.onPayloadReceived(peerId, replyPayload.toJsonString())
            }
        }
    }

    override fun setConnectionListener(listener: ConnectionListener) {
        this.connectionListener = listener
    }
}
