package MoonshotApp.MokshaSetu.data.services

import MoonshotApp.MokshaSetu.data.ClaimState
import MoonshotApp.MokshaSetu.data.FinancialAsset
import MoonshotApp.MokshaSetu.data.Nominee
import kotlinx.coroutines.delay

interface SettlementService {
    suspend fun submitClaim(
        asset: FinancialAsset,
        nominee: Nominee,
        onState: (ClaimState) -> Unit
    ): String
}

class MockSettlementService(private val stepDelayMs: Long = 900) : SettlementService {

    override suspend fun submitClaim(
        asset: FinancialAsset,
        nominee: Nominee,
        onState: (ClaimState) -> Unit
    ): String {
        onState(ClaimState.PACKET_SENT)
        delay(stepDelayMs)
        onState(ClaimState.INSTITUTION_PROCESSING)
        delay(stepDelayMs)
        onState(ClaimState.CREDITED)
        return referenceFor(asset, nominee)
    }

    private fun referenceFor(asset: FinancialAsset, nominee: Nominee): String {
        val institution = asset.institution.filter { it.isLetter() }.take(3).uppercase().ifBlank { "MSX" }
        return "MS-$institution-${asset.id.toString().padStart(4, '0')}-${nominee.id}"
    }
}
