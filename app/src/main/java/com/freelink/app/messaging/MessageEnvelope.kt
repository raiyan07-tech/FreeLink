package com.freelink.app.messaging

import org.json.JSONObject
import java.util.UUID

/**
 * Bounded wire envelope for FreeLink messages.
 */
data class MessageEnvelope(
    val messageId: String = UUID.randomUUID().toString(),
    val protocolVersion: Int = 1,
    val senderFlId: String,
    val recipientFlId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val expiryTimestamp: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
    val contentType: String = "text/plain", // or "ack"
    val payloadLength: Int,
    val ciphertextBase64: String,
    val ivBase64: String,
    val authTagOrHmac: String = ""
) {
    fun toJsonString(): String {
        val json = JSONObject()
        json.put("id", messageId)
        json.put("ver", protocolVersion)
        json.put("src", senderFlId)
        json.put("dst", recipientFlId)
        json.put("ts", timestamp)
        json.put("exp", expiryTimestamp)
        json.put("ct", contentType)
        json.put("len", payloadLength)
        json.put("payload", ciphertextBase64)
        json.put("iv", ivBase64)
        json.put("tag", authTagOrHmac)
        return json.toString()
    }

    companion object {
        const val MAX_WIRE_SIZE_BYTES = 4096

        fun fromJsonString(jsonStr: String): MessageEnvelope? {
            return try {
                if (jsonStr.toByteArray(Charsets.UTF_8).size > MAX_WIRE_SIZE_BYTES) {
                    return null // Reject oversized packet
                }
                val obj = JSONObject(jsonStr)
                MessageEnvelope(
                    messageId = obj.getString("id"),
                    protocolVersion = obj.getInt("ver"),
                    senderFlId = obj.getString("src"),
                    recipientFlId = obj.getString("dst"),
                    timestamp = obj.getLong("ts"),
                    expiryTimestamp = obj.getLong("exp"),
                    contentType = obj.getString("ct"),
                    payloadLength = obj.getInt("len"),
                    ciphertextBase64 = obj.getString("payload"),
                    ivBase64 = obj.getString("iv"),
                    authTagOrHmac = obj.optString("tag", "")
                )
            } catch (_: Exception) {
                null
            }
        }

        fun createAckEnvelope(originalMessageId: String, senderFlId: String, recipientFlId: String): MessageEnvelope {
            return MessageEnvelope(
                messageId = UUID.randomUUID().toString(),
                protocolVersion = 1,
                senderFlId = senderFlId,
                recipientFlId = recipientFlId,
                timestamp = System.currentTimeMillis(),
                expiryTimestamp = System.currentTimeMillis() + (60 * 60 * 1000L),
                contentType = "ack",
                payloadLength = originalMessageId.length,
                ciphertextBase64 = originalMessageId, // ACK payload is plain ID for simplicity
                ivBase64 = "",
                authTagOrHmac = ""
            )
        }
    }
}
