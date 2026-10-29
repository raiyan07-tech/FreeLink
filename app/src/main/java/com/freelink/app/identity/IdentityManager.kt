package com.freelink.app.identity

import android.content.Context
import android.content.SharedPreferences
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.util.Base64
import java.util.Locale

/**
 * Handles cryptographic identity and FL ID allocation.
 * Never stores or exports private keys in QR codes or plain logs.
 */
class IdentityManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("freelink_identity", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ALIAS = "freelink_identity_key"
        private const val PREF_FL_ID = "pref_local_fl_id"
        private const val PREF_PUB_KEY = "pref_local_pub_key"
        private const val PREF_IS_PROVISIONAL = "pref_is_provisional"

        /**
         * Validates that an FL ID is exactly 10 numeric digits.
         */
        fun isValidFlId(flId: String?): Boolean {
            if (flId.isNullOrBlank()) return false
            return flId.matches(Regex("^[1-9][0-9]{9}\$"))
        }

        /**
         * Computes a SHA-256 fingerprint from bytes, formatted into readable 4-character blocks.
         */
        fun computeFingerprint(data: ByteArray): String {
            val digest = MessageDigest.getInstance("SHA-256").digest(data)
            return digest.take(16).joinToString("") { "%02X".format(it) }
                .chunked(4)
                .joinToString(" ")
        }
    }

    /**
     * Retrieves or generates the 10-digit FreeLink ID.
     */
    fun getOrCreateFlId(): String {
        val existing = prefs.getString(PREF_FL_ID, null)
        if (existing != null && isValidFlId(existing)) {
            return existing
        }
        // Generate a provisional 10-digit ID (starting with 8 or 9)
        val rng = SecureRandom()
        val firstDigit = 8 + rng.nextInt(2) // 8 or 9 indicates provisional/offline
        val rest = (1..9).map { rng.nextInt(10) }.joinToString("")
        val newId = "$firstDigit$rest"
        prefs.edit().putString(PREF_FL_ID, newId).putBoolean(PREF_IS_PROVISIONAL, true).apply()
        return newId
    }

    /**
     * Returns true if the current FL ID was generated offline without central registry verification.
     */
    fun isProvisional(): Boolean = prefs.getBoolean(PREF_IS_PROVISIONAL, true)

    /**
     * Gets or generates the public/private key pair and returns the public key fingerprint.
     */
    fun getOrCreatePublicKeyFingerprint(): String {
        val pubKeyBase64 = prefs.getString(PREF_PUB_KEY, null)
        if (pubKeyBase64 != null) {
            val pubBytes = Base64.getDecoder().decode(pubKeyBase64)
            return computeFingerprint(pubBytes)
        }

        // Generate RSA 2048 or EC keypair
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        val keyPair = kpg.generateKeyPair()
        val pubBytes = keyPair.public.encoded
        val base64 = Base64.getEncoder().encodeToString(pubBytes)
        prefs.edit().putString(PREF_PUB_KEY, base64).apply()

        return computeFingerprint(pubBytes)
    }

    /**
     * Gets raw public key base64.
     */
    fun getPublicKeyBase64(): String {
        var pub = prefs.getString(PREF_PUB_KEY, null)
        if (pub == null) {
            getOrCreatePublicKeyFingerprint()
            pub = prefs.getString(PREF_PUB_KEY, "") ?: ""
        }
        return pub
    }

    /**
     * Updates to an officially verified FL ID assigned by the registry server.
     */
    fun setVerifiedFlId(verifiedId: String) {
        if (isValidFlId(verifiedId)) {
            prefs.edit()
                .putString(PREF_FL_ID, verifiedId)
                .putBoolean(PREF_IS_PROVISIONAL, false)
                .apply()
        }
    }
}
