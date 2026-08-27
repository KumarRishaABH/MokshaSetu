package MoonshotApp.MokshaSetu

import MoonshotApp.MokshaSetu.data.ClaimState
import MoonshotApp.MokshaSetu.data.DeathCertificate
import MoonshotApp.MokshaSetu.data.FinancialAsset
import MoonshotApp.MokshaSetu.data.Fixtures
import MoonshotApp.MokshaSetu.data.RegistryState
import MoonshotApp.MokshaSetu.data.services.MockAadhaarAuthService
import MoonshotApp.MokshaSetu.data.services.MockAccountDiscoveryService
import MoonshotApp.MokshaSetu.data.services.MockDeathRegistryService
import MoonshotApp.MokshaSetu.data.services.MockSettlementService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MockServicesTest {

    @Test
    fun discoveryEmitsEveryAssetInTheCatalogue() = runBlocking {
        val service = MockAccountDiscoveryService(stepDelayMs = 0)
        val found = mutableListOf<FinancialAsset>()

        service.discover(Fixtures.plannerProfile) { found.add(it) }

        assertEquals(service.expectedCount, found.size)
        assertEquals(Fixtures.discoverableAssets().map { it.id }, found.map { it.id })
    }

    @Test
    fun discoveryLeavesAtLeastTwoAssetsWithoutANominee() = runBlocking {
        val service = MockAccountDiscoveryService(stepDelayMs = 0)
        val found = mutableListOf<FinancialAsset>()

        service.discover(Fixtures.plannerProfile) { found.add(it) }

        assertTrue(found.count { it.nomineeId == null } >= 2)
    }

    @Test
    fun aadhaarAuthAcceptsTwelveDigitsAndTheDemoOtp() = runBlocking {
        val service = MockAadhaarAuthService(latencyMs = 0)

        val txnId = service.sendOtp(Fixtures.PLANNER_AADHAAR)
        assertNotNull(txnId)

        val profile = service.verifyOtp(txnId!!, Fixtures.DEMO_OTP)
        assertEquals(Fixtures.plannerProfile.name, profile?.name)
    }

    @Test
    fun aadhaarAuthRejectsShortNumbersAndWrongOtp() = runBlocking {
        val service = MockAadhaarAuthService(latencyMs = 0)

        assertNull(service.sendOtp("9012"))

        val txnId = service.sendOtp(Fixtures.NOMINEE_AADHAAR)
        assertNull(service.verifyOtp(txnId!!, "000000"))
    }

    @Test
    fun registryVerifiesTheRecordOnFile() = runBlocking {
        val service = MockDeathRegistryService(latencyMs = 0)

        assertEquals(RegistryState.VERIFIED, service.verify(Fixtures.deathCertificate))
    }

    @Test
    fun registryRejectsAWrongRegistrationNumber() = runBlocking {
        val service = MockDeathRegistryService(latencyMs = 0)
        val wrong = Fixtures.deathCertificate.copy(registrationNo = "MH/PUN/2026/0000001")

        assertEquals(RegistryState.MISMATCH, service.verify(wrong))
    }

    @Test
    fun registryRejectsTheRightNumberInTheWrongState() = runBlocking {
        val service = MockDeathRegistryService(latencyMs = 0)
        val wrongState: DeathCertificate = Fixtures.deathCertificate.copy(state = "Kerala")

        assertEquals(RegistryState.MISMATCH, service.verify(wrongState))
    }

    @Test
    fun settlementWalksToCreditedAndReturnsAReference() = runBlocking {
        val service = MockSettlementService(stepDelayMs = 0)
        val asset = Fixtures.discoverableAssets().first()
        val nominee = Fixtures.nominees().first()
        val states = mutableListOf<ClaimState>()

        val reference = service.submitClaim(asset, nominee) { states.add(it) }

        assertEquals(
            listOf(ClaimState.PACKET_SENT, ClaimState.INSTITUTION_PROCESSING, ClaimState.CREDITED),
            states
        )
        assertEquals(ClaimState.CREDITED, states.last())
        assertEquals("MS-HDF-0001-1", reference)
    }
}
