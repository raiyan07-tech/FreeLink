package com.freelink.app.messaging

import com.freelink.app.identity.IdentityManager
import java.util.concurrent.ConcurrentHashMap

/**
 * Protocol rules: deduplication, rate limiting, expiry, and payload bounds.
 */
class ProtocolEngine {

    // Deduplication cache: bounded LRU of message UUIDs seen within TTL.
    // Eldest entries are evicted instead of clearing the whole cache (no replay window).
    private val seenMessageIds = object : LinkedHashMap<String, Boolean>(16, 0.75f, false) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Boolean>): Boolean =
            size > DEDUP_CACHE_LIMIT
    }

    // Rate limiter: maps sender FL ID -> list of timestamps
    private val senderRateLimits = ConcurrentHashMap<String, MutableList<Long>>()

    companion object {
        const val MAX_TEXT_LENGTH = 1000 // FreeLink short text limit
        const val MAX_MESSAGES_PER_MINUTE = 15
        const val DEDUP_CACHE_LIMIT = 500
    }

    /**
     * Checks if packet is syntactically valid and complies with limits.
     */
    fun validateEnvelope(envelope: MessageEnvelope): ValidationResult {
        if (!IdentityManager.isValidFlId(envelope.senderFlId)) {
            return ValidationResult.Invalid("Sender FL ID format is invalid")
        }
        if (!IdentityManager.isValidFlId(envelope.recipientFlId)) {
            return ValidationResult.Invalid("Recipient FL ID format is invalid")
        }
        if (envelope.payloadLength > MessageEnvelope.MAX_WIRE_SIZE_BYTES) {
            return ValidationResult.Invalid("Payload length exceeds limit")
        }
        val now = System.currentTimeMillis()
        if (envelope.expiryTimestamp < now) {
            return ValidationResult.Expired("Message expired")
        }
        if (isDuplicate(envelope.messageId)) {
            return ValidationResult.Duplicate("Message already processed")
        }
        if (envelope.contentType != "ack" && isRateLimited(envelope.senderFlId, now)) {
            return ValidationResult.RateLimited("Peer rate limit exceeded")
        }
        return ValidationResult.Valid
    }

    /**
     * Records a message ID as processed.
     */
    fun markProcessed(messageId: String) {
        synchronized(seenMessageIds) {
            seenMessageIds[messageId] = true
        }
    }

    fun isDuplicate(messageId: String): Boolean {
        return synchronized(seenMessageIds) {
            seenMessageIds.containsKey(messageId)
        }
    }

    private fun isRateLimited(senderFlId: String, now: Long): Boolean {
        val windowStart = now - 60_000L
        val timestamps = senderRateLimits.computeIfAbsent(senderFlId) { mutableListOf() }
        synchronized(timestamps) {
            timestamps.removeAll { it < windowStart }
            if (timestamps.size >= MAX_MESSAGES_PER_MINUTE) {
                return true
            }
            timestamps.add(now)
            return false
        }
    }

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val reason: String) : ValidationResult()
        data class Expired(val reason: String) : ValidationResult()
        data class Duplicate(val reason: String) : ValidationResult()
        data class RateLimited(val reason: String) : ValidationResult()
    }
}
