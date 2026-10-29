package com.freelink.app.messaging

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Authenticated Encryption using AES-256-GCM.
 * Protects message payloads from eavesdropping and tampering.
 */
object CryptoEngine {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128

    data class EncryptedResult(
        val ciphertextBase64: String,
        val ivBase64: String
    )

    /**
     * Derives a 256-bit symmetric key deterministically from a shared secret, peer fingerprint, or passphrase.
     */
    fun deriveKey(seed: String): SecretKey {
        val digest = MessageDigest.getInstance("SHA-256").digest(seed.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(digest, ALGORITHM)
    }

    /**
     * Encrypts plaintext bytes using AES-256-GCM.
     */
    fun encrypt(plaintext: String, secretKey: SecretKey): EncryptedResult {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return EncryptedResult(
            ciphertextBase64 = Base64.getEncoder().encodeToString(ciphertext),
            ivBase64 = Base64.getEncoder().encodeToString(iv)
        )
    }

    /**
     * Decrypts ciphertext bytes using AES-256-GCM. Throws if tampered.
     */
    fun decrypt(ciphertextBase64: String, ivBase64: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = Base64.getDecoder().decode(ivBase64)
        val ciphertext = Base64.getDecoder().decode(ciphertextBase64)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val plaintextBytes = cipher.doFinal(ciphertext)
        return String(plaintextBytes, Charsets.UTF_8)
    }
}
