package MoonshotApp.MokshaSetu.data.services

import MoonshotApp.MokshaSetu.data.AadhaarProfile
import MoonshotApp.MokshaSetu.data.FinancialAsset
import MoonshotApp.MokshaSetu.data.Fixtures
import kotlinx.coroutines.delay

interface AccountDiscoveryService {
    val expectedCount: Int

    suspend fun discover(profile: AadhaarProfile, onFound: (FinancialAsset) -> Unit)
}

class MockAccountDiscoveryService(
    private val stepDelayMs: Long = 450,
    private val catalogue: List<FinancialAsset> = Fixtures.discoverableAssets()
) : AccountDiscoveryService {

    override val expectedCount: Int get() = catalogue.size

    override suspend fun discover(profile: AadhaarProfile, onFound: (FinancialAsset) -> Unit) {
        catalogue.forEach { asset ->
            delay(stepDelayMs)
            onFound(asset)
        }
    }
}
