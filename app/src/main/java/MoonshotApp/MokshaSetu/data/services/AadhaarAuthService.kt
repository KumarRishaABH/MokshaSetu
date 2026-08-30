package MoonshotApp.MokshaSetu.data.services

import MoonshotApp.MokshaSetu.data.AadhaarProfile
import MoonshotApp.MokshaSetu.data.Fixtures
import MoonshotApp.MokshaSetu.data.maskAadhaar
import MoonshotApp.MokshaSetu.data.remote.AadhaarRegistry
import kotlinx.coroutines.delay

interface AadhaarAuthService {
    suspend fun sendOtp(aadhaar: String): String?

    suspend fun verifyOtp(txnId: String, otp: String): AadhaarProfile?
}

class MockAadhaarAuthService(
    private val registry: AadhaarRegistry,
    private val latencyMs: Long = 700
) : AadhaarAuthService {

    private val issued = mutableMapOf<String, String>()

    override suspend fun sendOtp(aadhaar: String): String? {
        val digits = aadhaar.filter { it.isDigit() }
        if (digits.length != AADHAAR_LENGTH) return null
        val record = registry.findByNumber(digits) ?: return null
        if (!record.active) return null
        delay(latencyMs)
        val txnId = "UIDAI-${digits.takeLast(4)}-${issued.size + 1}"
        issued[txnId] = digits
        return txnId
    }

    override suspend fun verifyOtp(txnId: String, otp: String): AadhaarProfile? {
        val aadhaar = issued[txnId] ?: return null
        val record = registry.findByNumber(aadhaar) ?: return null
        delay(latencyMs)
        if (otp != Fixtures.DEMO_OTP) return null
        return AadhaarProfile(
            maskedAadhaar = maskAadhaar(aadhaar),
            name = record.holderName,
            dob = record.dob,
            address = record.address
        )
    }

    companion object {
        const val AADHAAR_LENGTH = 12
        const val OTP_LENGTH = 6
    }
}
