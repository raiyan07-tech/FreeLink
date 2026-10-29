package com.freelink.app.domain.models

/**
 * Honest delivery states for messages in FreeLink.
 * A message is never marked as "read" automatically, and "queued" is never disguised as "sent".
 */
enum class DeliveryState {
    QUEUED_LOCALLY,
    SENDING,
    SENT_TO_PEER,
    ACKNOWLEDGED_BY_PEER,
    FAILED
}

/**
 * Physical or network transport used for the exchange.
 */
enum class TransportType {
    BLUETOOTH_LE,
    WIFI_DIRECT,
    LOCAL_DEMO_SIMULATION,
    INTERNET_RELAY
}

/**
 * Verified connectivity state of the host device.
 */
enum class NetworkState {
    OFFLINE,
    CELLULAR_NO_DATA,
    LOCAL_RADIO_ONLY,
    INTERNET_AVAILABLE
}

/**
 * Trust status of a contact in FreeLink.
 */
enum class TrustStatus {
    UNVERIFIED,
    VERIFIED_FINGERPRINT,
    VERIFIED_QR,
    BLOCKED
}
