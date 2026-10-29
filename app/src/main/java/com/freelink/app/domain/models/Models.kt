package com.freelink.app.domain.models

import java.util.UUID

/**
 * Local device user profile and identity.
 */
data class LocalProfile(
    val uuid: String = UUID.randomUUID().toString(),
    val flId: String, // 10-digit FreeLink ID
    val displayName: String,
    val username: String,
    val publicKeyFingerprint: String,
    val isProvisional: Boolean = true, // Provisional until confirmed with online registry
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * A trusted or discovered contact.
 */
data class Contact(
    val id: String = UUID.randomUUID().toString(),
    val flId: String,
    val displayName: String,
    val username: String = "",
    val publicKeyFingerprint: String,
    val trustStatus: TrustStatus = TrustStatus.UNVERIFIED,
    val isBlocked: Boolean = false,
    val lastSeenTimestamp: Long = 0L,
    val notes: String = ""
)

/**
 * A chat message with cryptographic security and bounded delivery lifecycle.
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val conversationContactId: String,
    val senderFlId: String,
    val recipientFlId: String,
    val textContent: String,
    val timestamp: Long = System.currentTimeMillis(),
    val expiryTimestamp: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L), // Default 24h
    val deliveryState: DeliveryState = DeliveryState.QUEUED_LOCALLY,
    val transportType: TransportType = TransportType.BLUETOOTH_LE,
    val retryCount: Int = 0,
    val errorMessage: String? = null
)

/**
 * Complete offline travel kit stored entirely on the device.
 */
data class TravelKit(
    val id: Int = 1, // Single active profile kit
    val primaryOperator: String = "Airtel", // e.g., Airtel, Jio, Vi, BSNL
    val operatorUssdHelp: String = "*121#",
    val userRechargeNotes: String = "",
    val meetingPoint: String = "",
    val destinationAddress: String = "",
    val emergencyMedicalNotes: String = "",
    val bloodGroup: String = "",
    val emergencyContactName1: String = "",
    val emergencyContactPhone1: String = "",
    val emergencyContactName2: String = "",
    val emergencyContactPhone2: String = "",
    val offlineSosTemplate: String = "EMERGENCY: I am out of mobile balance. My last known location is near [Landmark]. Please reach out or assist.",
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Nearby discovered peer representation.
 */
data class PeerDevice(
    val id: String, // Device address or unique advertisement token
    val flId: String, // Public 10-digit ID
    val displayName: String,
    val rssi: Int = -70,
    val transportType: TransportType = TransportType.BLUETOOTH_LE,
    val publicKeyFingerprint: String = "",
    val isConnectionPending: Boolean = false,
    val isConnected: Boolean = false,
    val lastDiscoveredTimestamp: Long = System.currentTimeMillis()
)
