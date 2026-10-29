package com.freelink.app.online

data class OperatorInfo(
    val name: String,
    val ussdBalanceCheck: String,
    val customerCareNumber: String,
    val officialRechargeUrl: String,
    val note: String
)

object OperatorDirectory {
    val operators = listOf(
        OperatorInfo(
            name = "Airtel",
            ussdBalanceCheck = "*121#",
            customerCareNumber = "121",
            officialRechargeUrl = "https://www.airtel.in/recharge",
            note = "Dial *121# for USSD menu or 198 for complaints."
        ),
        OperatorInfo(
            name = "Reliance Jio",
            ussdBalanceCheck = "*333#",
            customerCareNumber = "1991",
            officialRechargeUrl = "https://www.jio.com/recharge",
            note = "Dial 1991 for balance & validity announcement via IVR."
        ),
        OperatorInfo(
            name = "Vodafone Idea (Vi)",
            ussdBalanceCheck = "*199#",
            customerCareNumber = "198",
            officialRechargeUrl = "https://www.myvi.in/prepaid/online-mobile-recharge",
            note = "Dial *199# for main balance and active service status."
        ),
        OperatorInfo(
            name = "BSNL",
            ussdBalanceCheck = "*123#",
            customerCareNumber = "1503",
            officialRechargeUrl = "https://portal.bsnl.in/myportal/",
            note = "Dial *123# for prepaid balance or 1503 for telecom support."
        )
    )

    val safeWifiTips = listOf(
        "Ask cafe/station reception for the legitimate Wi-Fi SSID and password to prevent Evil Twin attacks.",
        "Never enter your UPI PIN, ATM PIN, or banking passwords on captive login portals.",
        "Once connected, use only HTTPS links or official carrier applications to top up your account.",
        "Disconnect from public Wi-Fi immediately after completing your emergency recharge."
    )
}

interface RegistryClient {
    suspend fun verifyFlId(flId: String): RegistryVerificationResult
    suspend fun registerOnline(flId: String, publicKeyBase64: String): Boolean
}

sealed class RegistryVerificationResult {
    data class Verified(val flId: String, val registeredAt: Long) : RegistryVerificationResult()
    object NotFound : RegistryVerificationResult()
    data class Error(val message: String) : RegistryVerificationResult()
}

class MockRegistryClient : RegistryClient {
    override suspend fun verifyFlId(flId: String): RegistryVerificationResult {
        return if (flId.length == 10 && flId.all { it.isDigit() }) {
            RegistryVerificationResult.Verified(flId, System.currentTimeMillis() - 86400000L)
        } else {
            RegistryVerificationResult.NotFound
        }
    }

    override suspend fun registerOnline(flId: String, publicKeyBase64: String): Boolean {
        return true
    }
}
