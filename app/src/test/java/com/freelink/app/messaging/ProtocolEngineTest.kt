package com.freelink.app.messaging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ProtocolEngineTest {

    private lateinit var protocolEngine: ProtocolEngine

    @Before
    fun setup() {
        protocolEngine = ProtocolEngine()
    }

    @Test
    fun validEnvelope_passesValidation() {
        val envelope = MessageEnvelope(
            messageId = UUID.randomUUID().toString(),
            senderFlId = "9876543210",
            recipientFlId = "8123456789",
            payloadLength = 12,
            ciphertextBase64 = "SGVsbG8gV29ybGQ=",
            ivBase64 = "YWJjZGVmZ2hpams="
        )

        val result = protocolEngine.validateEnvelope(envelope)
        assertTrue(result is ProtocolEngine.ValidationResult.Valid)
    }

    @Test
    fun invalidSenderId_isRejected() {
        val envelope = MessageEnvelope(
            messageId = UUID.randomUUID().toString(),
            senderFlId = "123", // Invalid length
            recipientFlId = "8123456789",
            payloadLength = 10,
            ciphertextBase64 = "test",
            ivBase64 = "iv"
        )

        val result = protocolEngine.validateEnvelope(envelope)
        assertTrue(result is ProtocolEngine.ValidationResult.Invalid)
    }

    @Test
    fun expiredEnvelope_isRejected() {
        val envelope = MessageEnvelope(
            messageId = UUID.randomUUID().toString(),
            senderFlId = "9876543210",
            recipientFlId = "8123456789",
            timestamp = System.currentTimeMillis() - 100_000L,
            expiryTimestamp = System.currentTimeMillis() - 1000L, // Already expired
            payloadLength = 10,
            ciphertextBase64 = "test",
            ivBase64 = "iv"
        )

        val result = protocolEngine.validateEnvelope(envelope)
        assertTrue(result is ProtocolEngine.ValidationResult.Expired)
    }

    @Test
    fun duplicateEnvelope_isDetectedAndSuppressed() {
        val msgId = UUID.randomUUID().toString()
        val envelope = MessageEnvelope(
            messageId = msgId,
            senderFlId = "9876543210",
            recipientFlId = "8123456789",
            payloadLength = 10,
            ciphertextBase64 = "test",
            ivBase64 = "iv"
        )

        protocolEngine.markProcessed(msgId)
        val result = protocolEngine.validateEnvelope(envelope)
        assertTrue(result is ProtocolEngine.ValidationResult.Duplicate)
    }

    @Test
    fun oversizedPayload_isRejected() {
        val envelope = MessageEnvelope(
            messageId = UUID.randomUUID().toString(),
            senderFlId = "9876543210",
            recipientFlId = "8123456789",
            payloadLength = 5000, // Exceeds MAX_WIRE_SIZE_BYTES (4096)
            ciphertextBase64 = "test",
            ivBase64 = "iv"
        )

        val result = protocolEngine.validateEnvelope(envelope)
        assertTrue(result is ProtocolEngine.ValidationResult.Invalid)
    }

    @Test
    fun envelopeJsonSerializationRoundTrip() {
        val envelope = MessageEnvelope(
            messageId = "test-msg-id-1234",
            senderFlId = "9123456789",
            recipientFlId = "8987654321",
            payloadLength = 20,
            ciphertextBase64 = "CipherBytesBase64==",
            ivBase64 = "InitVector=="
        )

        val json = envelope.toJsonString()
        val parsed = MessageEnvelope.fromJsonString(json)

        assertNotNull(parsed)
        assertEquals("test-msg-id-1234", parsed?.messageId)
        assertEquals("9123456789", parsed?.senderFlId)
        assertEquals("8987654321", parsed?.recipientFlId)
        assertEquals(20, parsed?.payloadLength)
        assertEquals("CipherBytesBase64==", parsed?.ciphertextBase64)
    }
}
