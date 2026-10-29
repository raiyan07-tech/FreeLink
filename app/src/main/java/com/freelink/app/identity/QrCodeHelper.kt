package com.freelink.app.identity

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.json.JSONObject

/**
 * Public Contact Card representation for QR exchange.
 * Strictly contains public identity data only.
 */
data class PublicContactCard(
    val flId: String,
    val displayName: String,
    val publicKeyFingerprint: String,
    val publicKeyBase64: String = "",
    val protocolVersion: Int = 1
) {
    fun toJsonString(): String {
        val obj = JSONObject()
        obj.put("type", "freelink_card")
        obj.put("version", protocolVersion)
        obj.put("flId", flId)
        obj.put("name", displayName)
        obj.put("fingerprint", publicKeyFingerprint)
        obj.put("pubKey", publicKeyBase64)
        return obj.toString()
    }

    companion object {
        fun fromJsonString(jsonStr: String): PublicContactCard? {
            return try {
                val obj = JSONObject(jsonStr)
                if (obj.optString("type") != "freelink_card") return null
                val flId = obj.getString("flId")
                if (!IdentityManager.isValidFlId(flId)) return null
                val name = obj.optString("name", "Unknown Contact")
                val fingerprint = obj.optString("fingerprint", "")
                val pubKey = obj.optString("pubKey", "")
                val version = obj.optInt("version", 1)
                PublicContactCard(flId, name, fingerprint, pubKey, version)
            } catch (_: Exception) {
                null
            }
        }
    }
}

object QrCodeHelper {

    /**
     * Generates an Android Bitmap containing the QR code.
     */
    fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
        return try {
            val hints = mapOf(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M
            )
            val bitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            val darkColor = Color.parseColor("#0B0E14") // Near-black navy
            val lightColor = Color.parseColor("#FFFFFF") // White

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) darkColor else lightColor
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (_: Exception) {
            null
        }
    }
}
