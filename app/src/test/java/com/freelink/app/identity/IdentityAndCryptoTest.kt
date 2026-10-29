package com.freelink.app.identity

import com.freelink.app.messaging.CryptoEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IdentityAndCryptoTest {

    @Test
    fun validFlId_checksCorrectly() {
        assertTrue(IdentityManager.isValidFlId("9876543210"))
        assertTrue(IdentityManager.isValidFlId("8123456789"))
        assertTrue(IdentityManager.isValidFlId("1000000000"))

        // Invalid cases
        assertFalse(IdentityManager.isValidFlId("0123456789")) // Starts with 0
        assertFalse(IdentityManager.isValidFlId("987654321")) // 9 digits
        assertFalse(IdentityManager.isValidFlId("98765432100")) // 11 digits
        assertFalse(IdentityManager.isValidFlId("abcdefghij")) // Non-digits
        assertFalse(IdentityManager.isValidFlId(""))
        assertFalse(IdentityManager.isValidFlId(null))
    }

    @Test
    fun fingerprint_computesProperChunkedFormat() {
        val testBytes = "FREELINK_TEST_KEY_MATERIAL".toByteArray(Charsets.UTF_8)
        val fingerprint = IdentityManager.computeFingerprint(testBytes)

        assertNotNull(fingerprint)
        val chunks = fingerprint.split(" ")
        assertEquals(8, chunks.size) // 16 bytes = 32 hex chars / 4 = 8 chunks
        chunks.forEach { chunk ->
            assertEquals(4, chunk.length)
        }
    }

    @Test
    fun publicContactCard_roundTripSerialization() {
        val card = PublicContactCard(
            flId = "9876543210",
            displayName = "Kamran Test",
            publicKeyFingerprint = "A1B2 C3D4 E5F6 7890",
            publicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A"
        )

        val json = card.toJsonString()
        val parsed = PublicContactCard.fromJsonString(json)

        assertNotNull(parsed)
        assertEquals("9876543210", parsed?.flId)
        assertEquals("Kamran Test", parsed?.displayName)
        assertEquals("A1B2 C3D4 E5F6 7890", parsed?.publicKeyFingerprint)

        // Ensure sensitive private data is never present
        assertFalse(json.contains("privateKey"))
        assertFalse(json.contains("password"))
        assertFalse(json.contains("location"))
    }

    @Test
    fun cryptoEngine_encryptDecryptRoundTrip() {
        val plaintext = "Hello! Stranded at Delhi Station with no balance."
        val key = CryptoEngine.deriveKey("9876543210" + "8123456789")

        val encResult = CryptoEngine.encrypt(plaintext, key)
        assertNotNull(encResult.ciphertextBase64)
        assertNotNull(encResult.ivBase64)

        val decrypted = CryptoEngine.decrypt(encResult.ciphertextBase64, encResult.ivBase64, key)
        assertEquals(plaintext, decrypted)
    }

    @Test(expected = Exception::class)
    fun cryptoEngine_decryptWithWrongKey_fails() {
        val plaintext = "Secret travel message"
        val key1 = CryptoEngine.deriveKey("SeedOne")
        val key2 = CryptoEngine.deriveKey("SeedTwo")

        val encResult = CryptoEngine.encrypt(plaintext, key1)
        CryptoEngine.decrypt(encResult.ciphertextBase64, encResult.ivBase64, key2)
    }
}
