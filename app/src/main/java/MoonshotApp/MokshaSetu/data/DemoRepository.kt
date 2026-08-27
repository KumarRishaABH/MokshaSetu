package MoonshotApp.MokshaSetu.data

import MoonshotApp.MokshaSetu.data.services.AadhaarAuthService
import MoonshotApp.MokshaSetu.data.services.AccountDiscoveryService
import MoonshotApp.MokshaSetu.data.services.DeathRegistryService
import MoonshotApp.MokshaSetu.data.services.MockAadhaarAuthService
import MoonshotApp.MokshaSetu.data.services.MockAccountDiscoveryService
import MoonshotApp.MokshaSetu.data.services.MockDeathRegistryService
import MoonshotApp.MokshaSetu.data.services.MockSettlementService
import MoonshotApp.MokshaSetu.data.services.SettlementService
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object DemoRepository {

    val aadhaarAuth: AadhaarAuthService = MockAadhaarAuthService()
    val accountDiscovery: AccountDiscoveryService = MockAccountDiscoveryService()
    val deathRegistry: DeathRegistryService = MockDeathRegistryService()
    val settlement: SettlementService = MockSettlementService()

    var role by mutableStateOf(UserRole.PLANNER)
        private set

    var plannerProfile by mutableStateOf<AadhaarProfile?>(null)
        private set

    var nomineeProfile by mutableStateOf<AadhaarProfile?>(null)
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

    fun resetDemo() {
        role = UserRole.PLANNER
        plannerProfile = null
        nomineeProfile = null
        deathCert = null
        registryState = RegistryState.IDLE
        claims.clear()
        assets.replaceWith(Fixtures.assets())
        digitalIdentities.replaceWith(Fixtures.digitalIdentities())
        nominees.replaceWith(Fixtures.nominees())
        propertyDocs.replaceWith(Fixtures.propertyDocs())
        wishes.replaceWith(Fixtures.wishes())
    }

    fun enterRole(newRole: UserRole) {
        resetDemo()
        role = newRole
        if (newRole == UserRole.PLANNER) {
            assets.clear()
            propertyDocs.clear()
        }
    }

    fun signIn(forRole: UserRole, profile: AadhaarProfile) {
        if (forRole == UserRole.PLANNER) plannerProfile = profile else nomineeProfile = profile
    }

    fun onAssetDiscovered(asset: FinancialAsset) {
        if (assets.none { it.id == asset.id }) assets.add(asset)
    }

    fun nomineeById(id: Int?): Nominee? = if (id == null) null else nominees.firstOrNull { it.id == id }

    fun activeNominee(): Nominee? {
        val masked = nomineeProfile?.maskedAadhaar
        return nominees.firstOrNull { it.maskedAadhaar == masked } ?: nominees.firstOrNull()
    }

    fun unassignedAssets(): List<FinancialAsset> = assets.filter { it.nomineeId == null }

    fun assetsOf(nomineeId: Int): List<FinancialAsset> = assets.filter { it.nomineeId == nomineeId }

    fun digitalIdentitiesOf(nomineeId: Int): List<DigitalIdentity> =
        digitalIdentities.filter { it.nomineeId == nomineeId }

    fun totalMappedRupees(): Long = assets.sumOf { it.valueRupees }

    fun entitlementsFor(nomineeId: Int): Entitlements =
        entitlementsFor(nomineeId, assets.toList(), digitalIdentities.toList(), propertyDocs.toList())

    fun readinessScore(): Int {
        if (assets.isEmpty() && digitalIdentities.isEmpty()) return 0
        val assetPart = if (assets.isEmpty()) 0 else assets.count { it.nomineeId != null } * 50 / assets.size
        val digitalPart = if (digitalIdentities.isEmpty()) {
            0
        } else {
            digitalIdentities.count { it.nomineeId != null } * 25 / digitalIdentities.size
        }
        val wishPart = (wishes.size * 15 / 4).coerceAtMost(15)
        val paperPart = if (propertyDocs.isEmpty()) 0 else 10
        return (assetPart + digitalPart + wishPart + paperPart).coerceIn(0, 100)
    }

    fun assignAssetNominee(assetId: Int, nomineeId: Int?) {
        val index = assets.indexOfFirst { it.id == assetId }
        if (index >= 0) assets[index] = assets[index].copy(nomineeId = nomineeId)
        val docIndex = propertyDocs.indexOfFirst { it.id == assetId }
        if (docIndex >= 0) propertyDocs[docIndex] = propertyDocs[docIndex].copy(nomineeId = nomineeId)
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
        assets.add(
            FinancialAsset(
                id = id,
                kind = AssetKind.PROPERTY,
                institution = title,
                maskedId = fileName,
                valueRupees = valueRupees,
                nomineeId = nomineeId,
                discoveredVia = Fixtures.VIA_SELF
            )
        )
        propertyDocs.add(PropertyDoc(id, title, fileName, nomineeId))
    }

    fun propertyDocFor(assetId: Int): PropertyDoc? = propertyDocs.firstOrNull { it.id == assetId }

    fun addNominee(name: String, relation: String, aadhaarDigits: String) {
        val id = (nominees.maxOfOrNull { it.id } ?: 0) + 1
        nominees.add(Nominee(id, name, relation, maskAadhaar(aadhaarDigits), verified = false))
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
