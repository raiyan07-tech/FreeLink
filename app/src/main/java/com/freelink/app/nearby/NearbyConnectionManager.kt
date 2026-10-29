package com.freelink.app.nearby

import com.freelink.app.domain.models.Contact
import com.freelink.app.domain.models.DeliveryState
import com.freelink.app.domain.models.Message
import com.freelink.app.domain.models.PeerDevice
import com.freelink.app.domain.models.TransportType
import com.freelink.app.domain.models.TrustStatus
import com.freelink.app.domain.repository.ContactsRepository
import com.freelink.app.domain.repository.MessageRepository
import com.freelink.app.identity.IdentityManager
import com.freelink.app.messaging.CryptoEngine
import com.freelink.app.messaging.MessageEnvelope
import com.freelink.app.messaging.ProtocolEngine
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class NearbyUiState(
    val isScanning: Boolean = false,
    val isAdvertising: Boolean = false,
    val activeTransportType: TransportType = TransportType.BLUETOOTH_LE,
    val discoveredPeers: List<PeerDevice> = emptyList(),
    val connectedPeer: PeerDevice? = null,
    val incomingConsentPeer: PeerDevice? = null,
    val statusMessage: String? = null,
    val isDemoModeActive: Boolean = false
)

class NearbyConnectionManager(
    private val bleTransport: BluetoothLeTransport,
    private val simulationTransport: SimulationTransport,
    private val identityManager: IdentityManager,
    private val contactsRepository: ContactsRepository,
    private val messageRepository: MessageRepository,
    private val scope: CoroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.w("FreeLink", "Nearby background task failed", throwable)
        }
    )
) : DiscoveryListener, ConnectionListener {

    private val protocolEngine = ProtocolEngine()

    // Consent callback for an inbound connection request awaiting user decision.
    private var pendingConsentDecision: ((Boolean) -> Unit)? = null

    private val _uiState = MutableStateFlow(NearbyUiState())
    val uiState: StateFlow<NearbyUiState> = _uiState.asStateFlow()

    private var activeTransport: NearbyTransport = bleTransport

    init {
        bleTransport.setConnectionListener(this)
        simulationTransport.setConnectionListener(this)
    }

    fun setDemoMode(enabled: Boolean) {
        stopScanning()
        activeTransport = if (enabled) simulationTransport else bleTransport
        _uiState.value = _uiState.value.copy(
            isDemoModeActive = enabled,
            activeTransportType = activeTransport.transportType,
            discoveredPeers = emptyList(),
            connectedPeer = null,
            statusMessage = if (enabled) "Developer Demo Simulation Active" else "BLE Radio Active"
        )
    }

    fun startScanning() {
        _uiState.value = _uiState.value.copy(
            isScanning = true,
            statusMessage = "Scanning for nearby FreeLink peers...",
            discoveredPeers = emptyList()
        )
        activeTransport.startDiscovery(this)
    }

    fun stopScanning() {
        activeTransport.stopDiscovery()
        _uiState.value = _uiState.value.copy(
            isScanning = false,
            statusMessage = "Scanning stopped."
        )
    }

    fun startAdvertising() {
        val flId = identityManager.getOrCreateFlId()
        activeTransport.startAdvertising(flId, "FreeLink-$flId")
        _uiState.value = _uiState.value.copy(isAdvertising = true)
    }

    fun stopAdvertising() {
        activeTransport.stopAdvertising()
        _uiState.value = _uiState.value.copy(isAdvertising = false)
    }

    fun requestConnection(peer: PeerDevice) {
        _uiState.value = _uiState.value.copy(
            statusMessage = "Connecting to ${peer.displayName}..."
        )
        activeTransport.connectToPeer(peer, this)
    }

    fun respondToConsent(accept: Boolean) {
        val peer = _uiState.value.incomingConsentPeer
        val decision = pendingConsentDecision
        pendingConsentDecision = null
        _uiState.value = _uiState.value.copy(incomingConsentPeer = null)
        if (peer == null) return
        if (accept) {
            if (decision != null) {
                // The transport completes the link and reports it via onConnected()
                decision(true)
            } else {
                _uiState.value = _uiState.value.copy(
                    connectedPeer = peer.copy(isConnected = true),
                    statusMessage = "Connected to ${peer.displayName}"
                )
            }
        } else {
            decision?.invoke(false)
            activeTransport.disconnect(peer.id)
            _uiState.value = _uiState.value.copy(statusMessage = "Connection rejected")
        }
    }

    fun disconnectPeer() {
        val peer = _uiState.value.connectedPeer ?: return
        activeTransport.disconnect(peer.id)
        _uiState.value = _uiState.value.copy(
            connectedPeer = null,
            statusMessage = "Disconnected from ${peer.displayName}"
        )
    }

    fun sendMessage(
        recipientContactId: String,
        recipientFlId: String,
        text: String,
        targetPeerId: String? = null,
        existingMessageId: String? = null
    ) {
        scope.launch {
            val localFlId = identityManager.getOrCreateFlId()

            // Store against the canonical Room contact (resolved by FL ID) so
            // outgoing and incoming messages land in the same conversation thread.
            val conversationId = contactsRepository.getContactByFlId(recipientFlId).firstOrNull()?.id
                ?: recipientContactId

            val message = if (existingMessageId == null) {
                val fresh = Message(
                    conversationContactId = conversationId,
                    senderFlId = localFlId,
                    recipientFlId = recipientFlId,
                    textContent = text,
                    deliveryState = DeliveryState.QUEUED_LOCALLY,
                    transportType = activeTransport.transportType
                )
                messageRepository.insertMessage(fresh)
                fresh
            } else {
                // Retry reuses the original message row instead of duplicating it.
                messageRepository.updateDeliveryState(existingMessageId, DeliveryState.SENDING)
                Message(
                    id = existingMessageId,
                    conversationContactId = conversationId,
                    senderFlId = localFlId,
                    recipientFlId = recipientFlId,
                    textContent = text,
                    deliveryState = DeliveryState.SENDING,
                    transportType = activeTransport.transportType
                )
            }

            val peerId = targetPeerId ?: _uiState.value.connectedPeer?.id
            if (peerId == null) {
                // Remains queued locally until a link is found
                messageRepository.updateDeliveryState(message.id, DeliveryState.QUEUED_LOCALLY)
                _uiState.value = _uiState.value.copy(statusMessage = "Message queued locally (no active peer)")
                return@launch
            }

            transmit(message, peerId)
        }
    }

    private suspend fun transmit(message: Message, peerId: String) {
        // Canonical symmetric key derivation shared by both sides: sender FL ID + recipient FL ID.
        val symmetricKey = CryptoEngine.deriveKey(message.senderFlId + message.recipientFlId)
        val enc = CryptoEngine.encrypt(message.textContent, symmetricKey)

        val envelope = MessageEnvelope(
            messageId = message.id,
            senderFlId = message.senderFlId,
            recipientFlId = message.recipientFlId,
            payloadLength = message.textContent.length,
            ciphertextBase64 = enc.ciphertextBase64,
            ivBase64 = enc.ivBase64
        )

        messageRepository.updateDeliveryState(message.id, DeliveryState.SENDING)

        activeTransport.sendPayload(peerId, envelope.toJsonString()) { success, errorMsg ->
            scope.launch {
                if (success) {
                    messageRepository.updateDeliveryState(message.id, DeliveryState.SENT_TO_PEER)
                    _uiState.value = _uiState.value.copy(statusMessage = "Message sent to peer, waiting for ACK")
                } else {
                    messageRepository.updateDeliveryState(
                        message.id,
                        DeliveryState.FAILED,
                        errorMsg ?: "Failed to transmit payload"
                    )
                    _uiState.value = _uiState.value.copy(statusMessage = "Send failed: $errorMsg")
                }
            }
        }
    }

    // Retransmit messages that were queued while no peer link existed.
    private fun flushQueuedMessages(peer: PeerDevice) {
        if (!IdentityManager.isValidFlId(peer.flId)) return
        scope.launch {
            val localFlId = identityManager.getOrCreateFlId()
            val queued = messageRepository.getQueuedMessages().firstOrNull() ?: return@launch
            queued
                .filter { it.recipientFlId == peer.flId && it.senderFlId == localFlId }
                .forEach { transmit(it, peer.id) }
        }
    }

    // --- DiscoveryListener Callbacks ---

    override fun onPeerDiscovered(peer: PeerDevice) {
        val currentList = _uiState.value.discoveredPeers.toMutableList()
        val index = currentList.indexOfFirst { it.id == peer.id }
        if (index >= 0) {
            currentList[index] = peer
        } else {
            currentList.add(peer)
        }
        _uiState.value = _uiState.value.copy(
            discoveredPeers = currentList,
            statusMessage = "Discovered peer: ${peer.displayName}"
        )
    }

    override fun onDiscoveryFailed(errorCode: Int, message: String) {
        _uiState.value = _uiState.value.copy(
            isScanning = false,
            statusMessage = "Discovery Error ($errorCode): $message"
        )
    }

    override fun onDiscoveryStopped() {
        _uiState.value = _uiState.value.copy(isScanning = false)
    }

    // --- ConnectionListener Callbacks ---

    override fun onConnectionRequested(peer: PeerDevice, onConsentDecision: (Boolean) -> Unit) {
        // Explicit consent required before the link is established
        pendingConsentDecision = onConsentDecision
        _uiState.value = _uiState.value.copy(incomingConsentPeer = peer)
    }

    override fun onConnected(peerId: String) {
        val peer = _uiState.value.discoveredPeers.find { it.id == peerId } ?: PeerDevice(
            id = peerId,
            flId = "Unknown",
            displayName = "Connected Device",
            isConnected = true
        )
        _uiState.value = _uiState.value.copy(
            connectedPeer = peer.copy(isConnected = true),
            statusMessage = "Connected to ${peer.displayName}"
        )
        // Push out anything the user queued while offline
        flushQueuedMessages(peer)
    }

    override fun onDisconnected(peerId: String) {
        if (_uiState.value.connectedPeer?.id == peerId) {
            _uiState.value = _uiState.value.copy(
                connectedPeer = null,
                statusMessage = "Disconnected from peer"
            )
        }
    }

    override fun onPayloadReceived(peerId: String, payload: String) {
        scope.launch {
            val envelope = MessageEnvelope.fromJsonString(payload) ?: return@launch

            // Only process (and acknowledge) traffic actually addressed to this device
            if (envelope.recipientFlId != identityManager.getOrCreateFlId()) {
                return@launch
            }

            // Validate against protocol rules (dedup, rate-limit, expiry)
            when (val validation = protocolEngine.validateEnvelope(envelope)) {
                is ProtocolEngine.ValidationResult.Duplicate -> {
                    // Send back ACK in case previous ACK was dropped
                    if (envelope.contentType != "ack") {
                        val ack = MessageEnvelope.createAckEnvelope(
                            envelope.messageId,
                            envelope.recipientFlId,
                            envelope.senderFlId
                        )
                        activeTransport.sendPayload(peerId, ack.toJsonString()) { _, _ -> }
                    }
                    return@launch
                }
                is ProtocolEngine.ValidationResult.Expired,
                is ProtocolEngine.ValidationResult.Invalid,
                is ProtocolEngine.ValidationResult.RateLimited -> {
                    return@launch // Reject packet
                }
                ProtocolEngine.ValidationResult.Valid -> {
                    protocolEngine.markProcessed(envelope.messageId)
                }
            }

            // Check if peer is blocked
            val existingContact = contactsRepository.getContactByFlId(envelope.senderFlId).firstOrNull()
            if (existingContact?.isBlocked == true) {
                return@launch // Ignore messages from blocked contacts
            }

            if (envelope.contentType == "ack") {
                // Acknowledged! Update the corresponding message
                val originalMessageId = envelope.ciphertextBase64
                messageRepository.updateDeliveryState(
                    originalMessageId,
                    DeliveryState.ACKNOWLEDGED_BY_PEER
                )
                return@launch
            }

            // Decrypt payload
            val symmetricKey = CryptoEngine.deriveKey(envelope.senderFlId + envelope.recipientFlId)
            val decryptedText = try {
                if (envelope.ivBase64.isNotEmpty()) {
                    CryptoEngine.decrypt(envelope.ciphertextBase64, envelope.ivBase64, symmetricKey)
                } else {
                    envelope.ciphertextBase64 // Plaintext fallback for simulated unencrypted envelopes
                }
            } catch (_: Exception) {
                "[Encrypted Message - Key Mismatch]"
            }

            // Ensure contact exists in local store
            val contactId = existingContact?.id ?: run {
                val newContact = Contact(
                    flId = envelope.senderFlId,
                    displayName = "Peer ${envelope.senderFlId.takeLast(4)}",
                    publicKeyFingerprint = "Unverified",
                    trustStatus = TrustStatus.UNVERIFIED,
                    lastSeenTimestamp = System.currentTimeMillis()
                )
                contactsRepository.insertOrUpdate(newContact)
                newContact.id
            }

            val incomingMessage = Message(
                id = envelope.messageId,
                conversationContactId = contactId,
                senderFlId = envelope.senderFlId,
                recipientFlId = envelope.recipientFlId,
                textContent = decryptedText,
                timestamp = envelope.timestamp,
                expiryTimestamp = envelope.expiryTimestamp,
                deliveryState = DeliveryState.RECEIVED,
                transportType = activeTransport.transportType
            )
            messageRepository.insertMessage(incomingMessage)

            // Send protocol acknowledgement back to peer
            val ack = MessageEnvelope.createAckEnvelope(
                originalMessageId = envelope.messageId,
                senderFlId = envelope.recipientFlId,
                recipientFlId = envelope.senderFlId
            )
            activeTransport.sendPayload(peerId, ack.toJsonString()) { _, _ -> }
        }
    }

    override fun onError(errorMessage: String) {
        _uiState.value = _uiState.value.copy(statusMessage = "Nearby Error: $errorMessage")
    }
}
