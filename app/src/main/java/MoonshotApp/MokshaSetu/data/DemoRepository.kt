package MoonshotApp.MokshaSetu.data

import MoonshotApp.MokshaSetu.data.services.AadhaarAuthService
import MoonshotApp.MokshaSetu.data.services.AccountDiscoveryService
import MoonshotApp.MokshaSetu.data.services.DeathRegistryService
import MoonshotApp.MokshaSetu.data.services.MockAadhaarAuthService
import MoonshotApp.MokshaSetu.data.services.MockAccountDiscoveryService
import MoonshotApp.MokshaSetu.data.services.MockDeathRegistryService
import MoonshotApp.MokshaSetu.data.services.MockSettlementService
import MoonshotApp.MokshaSetu.data.services.SettlementService
import MoonshotApp.MokshaSetu.data.remote.AadhaarRecord
import MoonshotApp.MokshaSetu.data.remote.AadhaarRegistry
import MoonshotApp.MokshaSetu.data.remote.RemoteAadhaarRegistry
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DemoRepository {

    val aadhaarRegistry: AadhaarRegistry = RemoteAadhaarRegistry()
    val aadhaarAuth: AadhaarAuthService = MockAadhaarAuthService(aadhaarRegistry)
    val accountDiscovery: AccountDiscoveryService = MockAccountDiscoveryService()
    val deathRegistry: DeathRegistryService = MockDeathRegistryService()
    val settlement: SettlementService = MockSettlementService()

    var dataMode by mutableStateOf(DataMode.SCRATCH)
        private set

    var role by mutableStateOf(UserRole.PLANNER)
        private set

    var plannerProfile by mutableStateOf<AadhaarProfile?>(null)
        private set

    var nomineeProfile by mutableStateOf<AadhaarProfile?>(null)
        private set

    var vaultOwnerName by mutableStateOf<String?>(null)
        private set

    val assets = mutableStateListOf<FinancialAsset>()
    val digitalIdentities = mutableStateListOf<DigitalIdentity>()
    val nominees = mutableStateListOf<Nominee>()
    val propertyDocs = mutableStateListOf<PropertyDoc>()
    val wishes = mutableStateListOf<Wish>()
    val claims = mutableStateListOf<Claim>()

    var deathCert by mutableStateOf<DeathCertificate?>(null)
        private set

    var registryState by mutableStateOf(RegistryState.IDLE)
        private set

    init {
        resetDemo()
    }

    fun chooseDataMode(mode: DataMode) {
        if (mode != dataMode) resetDemo(mode)
    }

    fun resetDemo(mode: DataMode = dataMode) {
        dataMode = mode
        resetIdentity()
        vaultOwnerName = if (mode == DataMode.DEMO) Fixtures.plannerProfile.name else null
        if (mode == DataMode.DEMO) {
            assets.replaceWith(Fixtures.assets())
            digitalIdentities.replaceWith(Fixtures.digitalIdentities())
            nominees.replaceWith(Fixtures.nominees())
            propertyDocs.replaceWith(Fixtures.propertyDocs())
            wishes.replaceWith(Fixtures.wishes())
        } else {
            assets.clear()
            digitalIdentities.clear()
            nominees.clear()
            propertyDocs.clear()
            wishes.clear()
        }
    }

    fun resetIdentity() {
        role = UserRole.PLANNER
        plannerProfile = null
        nomineeProfile = null
        deathCert = null
        registryState = RegistryState.IDLE
        claims.clear()
    }

    fun resetForRoleSwitch() {
        if (dataMode == DataMode.SCRATCH) resetIdentity() else resetDemo(DataMode.DEMO)
    }

    fun enterRole(newRole: UserRole) {
        if (dataMode == DataMode.DEMO) resetDemo(DataMode.DEMO) else resetIdentity()
        role = newRole
        if (newRole == UserRole.PLANNER && dataMode == DataMode.DEMO) {
            assets.clear()
            propertyDocs.clear()
        }
    }

    fun signIn(forRole: UserRole, profile: AadhaarProfile) {
        if (forRole == UserRole.PLANNER) {
            plannerProfile = profile
            vaultOwnerName = profile.name
        } else {
            nomineeProfile = profile
        }
    }

    fun onAssetDiscovered(asset: FinancialAsset) {
        if (assets.any { it.id == asset.id }) return
        val incoming = if (dataMode == DataMode.SCRATCH) asset.copy(splits = emptyList()) else asset
        assets.add(incoming)
    }

    fun nomineeById(id: Int?): Nominee? = if (id == null) null else nominees.firstOrNull { it.id == id }

    fun activeNominee(): Nominee? {
        val masked = nomineeProfile?.maskedAadhaar ?: return null
        return nominees.firstOrNull { it.maskedAadhaar == masked }
    }

    fun unassignedAssets(): List<FinancialAsset> = assets.filter { !it.isAssigned }

    fun assetsOf(nomineeId: Int): List<FinancialAsset> = assets.filter { it.splitFor(nomineeId) != null }

    fun digitalIdentitiesOf(nomineeId: Int): List<DigitalIdentity> =
        digitalIdentities.filter { it.nomineeId == nomineeId }

    fun totalMappedRupees(): Long = assets.sumOf { it.valueRupees }

    fun entitlementsFor(nomineeId: Int): Entitlements =
        entitlementsFor(nomineeId, assets.toList(), digitalIdentities.toList(), propertyDocs.toList())

    fun readinessScore(): Int {
        if (assets.isEmpty() && digitalIdentities.isEmpty()) return 0
        val assetPart = if (assets.isEmpty()) 0 else assets.count { it.isAssigned } * 50 / assets.size
        val digitalPart = if (digitalIdentities.isEmpty()) {
            0
        } else {
            digitalIdentities.count { it.nomineeId != null } * 25 / digitalIdentities.size
        }
        val wishPart = (wishes.size * 15 / 4).coerceAtMost(15)
        val paperPart = if (propertyDocs.isEmpty()) 0 else 10
        return (assetPart + digitalPart + wishPart + paperPart).coerceIn(0, 100)
    }

    fun assignAssetSplits(assetId: Int, splits: List<NomineeSplit>) {
        val index = assets.indexOfFirst { it.id == assetId }
        if (index >= 0) assets[index] = assets[index].copy(splits = splits)
        val docIndex = propertyDocs.indexOfFirst { it.id == assetId }
        if (docIndex >= 0) propertyDocs[docIndex] = propertyDocs[docIndex].copy(splits = splits)
    }

    fun assignDigitalNominee(identityId: Int, nomineeId: Int?) {
        val index = digitalIdentities.indexOfFirst { it.id == identityId }
        if (index >= 0) digitalIdentities[index] = digitalIdentities[index].copy(nomineeId = nomineeId)
    }

    fun addDigitalIdentity(
        platform: String,
        username: String,
        password: String,
        nomineeId: Int?,
        action: DigitalAction
    ) {
        val id = (digitalIdentities.maxOfOrNull { it.id } ?: 0) + 1
        digitalIdentities.add(
            DigitalIdentity(
                id = id,
                platform = platform,
                emoji = emojiFor(platform),
                username = username,
                password = password,
                nomineeId = nomineeId,
                afterDeathAction = action
            )
        )
    }

    fun addProperty(title: String, fileName: String, valueRupees: Long, nomineeId: Int?) {
        val id = (assets.maxOfOrNull { it.id } ?: 0) + 1
        val splits = listOfNotNull(nomineeId?.let { NomineeSplit(it, 100) })
        assets.add(
            FinancialAsset(
                id = id,
                kind = AssetKind.PROPERTY,
                institution = title,
                maskedId = fileName,
                valueRupees = valueRupees,
                splits = splits,
                discoveredVia = Fixtures.VIA_SELF
            )
        )
        propertyDocs.add(PropertyDoc(id, title, fileName, splits))
    }

    fun propertyDocFor(assetId: Int): PropertyDoc? = propertyDocs.firstOrNull { it.id == assetId }

    fun addNominee(name: String, relation: String, aadhaarDigits: String) {
        val id = (nominees.maxOfOrNull { it.id } ?: 0) + 1
        val digits = aadhaarDigits.filter { it.isDigit() }
        nominees.add(
            Nominee(
                id = id,
                name = name,
                relation = relation,
                maskedAadhaar = maskAadhaar(digits),
                verified = false,
                demoAadhaar = digits.takeIf { it.length == 12 }
            )
        )
        if (digits.length == 12) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    aadhaarRegistry.upsert(
                        AadhaarRecord(
                            aadhaarNumber = digits,
                            holderName = name,
                            dob = "",
                            address = "",
                            mobileLast4 = "0000",
                            active = true
                        )
                    )
                } catch (e: Exception) {
                    Log.w("DemoRepository", "Nominee registry upsert failed", e)
                }
            }
        }
    }

    fun suggestNomineeAadhaar(): String {
        var candidate = 450_000_000_000L + nominees.size
        while (nominees.any { it.demoAadhaar == candidate.toString() }) candidate++
        return candidate.toString()
    }

    fun addWish(text: String, recipient: String) {
        val id = (wishes.maxOfOrNull { it.id } ?: 0) + 1
        wishes.add(Wish(id, null, text, MetaKey.ROHAN, recipient))
    }

    fun setDeathCertificate(cert: DeathCertificate) {
        deathCert = cert
        registryState = RegistryState.IDLE
    }

    fun updateRegistryState(state: RegistryState) {
        registryState = state
    }

    fun claimFor(assetId: Int): Claim =
        claims.firstOrNull { it.assetId == assetId } ?: Claim(assetId, ClaimState.NOT_STARTED, null)

    fun updateClaim(assetId: Int, state: ClaimState, referenceNo: String? = null) {
        val index = claims.indexOfFirst { it.assetId == assetId }
        val existing = if (index >= 0) claims[index] else null
        val next = Claim(assetId, state, referenceNo ?: existing?.referenceNo)
        if (index >= 0) claims[index] = next else claims.add(next)
    }

    private fun emojiFor(platform: String): String = when {
        platform.contains("insta", ignoreCase = true) -> "📸"
        platform.contains("google", ignoreCase = true) -> "🔑"
        platform.contains("mail", ignoreCase = true) -> "✉️"
        platform.contains("whats", ignoreCase = true) -> "💬"
        platform.contains("face", ignoreCase = true) -> "👥"
        platform.contains("netflix", ignoreCase = true) -> "📺"
        platform.contains("bank", ignoreCase = true) -> "🏦"
        else -> "🌐"
    }

    private fun <T> MutableList<T>.replaceWith(items: List<T>) {
        clear()
        addAll(items)
    }
}
