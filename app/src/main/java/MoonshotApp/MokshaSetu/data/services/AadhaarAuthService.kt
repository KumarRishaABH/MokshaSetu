package MoonshotApp.MokshaSetu.data.services

import MoonshotApp.MokshaSetu.data.AadhaarProfile
import MoonshotApp.MokshaSetu.data.Fixtures
import kotlinx.coroutines.delay

interface AadhaarAuthService {
    suspend fun sendOtp(aadhaar: String): String?

    suspend fun verifyOtp(txnId: String, otp: String): AadhaarProfile?
}

class MockAadhaarAuthService(private val latencyMs: Long = 700) : AadhaarAuthService {

    private val issued = mutableMapOf<String, String>()

    override suspend fun sendOtp(aadhaar: String): String? {
        val digits = aadhaar.filter { it.isDigit() }
        if (digits.length != AADHAAR_LENGTH) return null
        delay(latencyMs)
        val txnId = "UIDAI-${digits.takeLast(4)}-${issued.size + 1}"
        issued[txnId] = digits
        return txnId
    }

    override suspend fun verifyOtp(txnId: String, otp: String): AadhaarProfile? {
        val aadhaar = issued[txnId] ?: return null
        delay(latencyMs)
        if (otp != Fixtures.DEMO_OTP) return null
        return Fixtures.profileFor(aadhaar)
    }

    companion object {
        const val AADHAAR_LENGTH = 12
        const val OTP_LENGTH = 6
    }
}
