package MoonshotApp.MokshaSetu.data

enum class UserRole { PLANNER, NOMINEE }

enum class DataMode { SCRATCH, DEMO }

enum class AssetKind { BANK, DEMAT, INSURANCE, PROPERTY }

enum class DigitalAction { MEMORIALISE, DELETE, TRANSFER_ACCESS }

enum class RegistryState { IDLE, CHECKING, VERIFIED, MISMATCH }

enum class ClaimState { NOT_STARTED, PACKET_SENT, INSTITUTION_PROCESSING, CREDITED }

data class AadhaarProfile(
    val maskedAadhaar: String,
    val name: String,
    val dob: String,
    val address: String
)

data class NomineeSplit(val nomineeId: Int, val percent: Int)

data class FinancialAsset(
    val id: Int,
    val kind: AssetKind,
    val institution: String,
    val maskedId: String,
    val valueRupees: Long,
    val splits: List<NomineeSplit> = emptyList(),
    val discoveredVia: String
) {
    val isAssigned: Boolean get() = splits.isNotEmpty()
    val registeredPercent: Int get() = splits.sumOf { it.percent }
    val unregisteredPercent: Int get() = (100 - registeredPercent).coerceAtLeast(0)

    fun splitFor(nomineeId: Int): NomineeSplit? = splits.firstOrNull { it.nomineeId == nomineeId }

    fun shareRupeesFor(nomineeId: Int): Long = valueRupees * (splitFor(nomineeId)?.percent ?: 0) / 100
}

data class DigitalIdentity(
    val id: Int,
    val platform: String,
    val emoji: String,
    val username: String,
    val password: String,
    val nomineeId: Int?,
    val afterDeathAction: DigitalAction
)

data class Nominee(
    val id: Int,
    val name: String,
    val relation: String,
    val maskedAadhaar: String,
    val verified: Boolean,
    val demoAadhaar: String? = null
) {
    val initial: String get() = name.trim().take(1).uppercase().ifBlank { "?" }
}

data class PropertyDoc(
    val id: Int,
    val title: String,
    val fileName: String,
    val splits: List<NomineeSplit> = emptyList()
)

data class DeathCertificate(
    val registrationNo: String,
    val state: String,
    val issuedOn: String,
    val deceasedName: String,
    val fileName: String?
)

data class Claim(
    val assetId: Int,
    val state: ClaimState,
    val referenceNo: String?
)

data class Wish(
    val id: Int,
    val textRes: Int?,
    val customText: String?,
    val metaRes: MetaKey,
    val customMeta: String?
)

enum class MetaKey { ROHAN, TEMPLE, INSTA, VIDEO }

fun maskAadhaar(digits: String): String {
    val clean = digits.filter { it.isDigit() }
    return if (clean.length < 4) "XXXX XXXX XXXX" else "XXXX XXXX ${clean.takeLast(4)}"
}
