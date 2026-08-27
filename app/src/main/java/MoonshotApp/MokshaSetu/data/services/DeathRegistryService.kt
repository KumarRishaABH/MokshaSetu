package MoonshotApp.MokshaSetu.data.services

import MoonshotApp.MokshaSetu.data.DeathCertificate
import MoonshotApp.MokshaSetu.data.Fixtures
import MoonshotApp.MokshaSetu.data.RegistryState
import kotlinx.coroutines.delay

interface DeathRegistryService {
    suspend fun verify(cert: DeathCertificate): RegistryState
}

class MockDeathRegistryService(
    private val latencyMs: Long = 900,
    private val onRecord: DeathCertificate = Fixtures.deathCertificate
) : DeathRegistryService {

    override suspend fun verify(cert: DeathCertificate): RegistryState {
        delay(latencyMs)
        val sameNumber = cert.registrationNo.trim().equals(onRecord.registrationNo, ignoreCase = true)
        val sameState = cert.state.trim().equals(onRecord.state, ignoreCase = true)
        return if (sameNumber && sameState) RegistryState.VERIFIED else RegistryState.MISMATCH
    }
}
