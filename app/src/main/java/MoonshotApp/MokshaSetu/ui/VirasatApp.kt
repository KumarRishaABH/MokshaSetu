package MoonshotApp.MokshaSetu.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.UserRole
import MoonshotApp.MokshaSetu.ui.screens.AadhaarLoginScreen
import MoonshotApp.MokshaSetu.ui.screens.AssetsScreen
import MoonshotApp.MokshaSetu.ui.screens.DigitalVaultScreen
import MoonshotApp.MokshaSetu.ui.screens.DiscoveryScreen
import MoonshotApp.MokshaSetu.ui.screens.HomeScreen
import MoonshotApp.MokshaSetu.ui.screens.NomineesScreen
import MoonshotApp.MokshaSetu.ui.screens.PropertyUploadScreen
import MoonshotApp.MokshaSetu.ui.screens.RoleChooserScreen
import MoonshotApp.MokshaSetu.ui.screens.SafetyNetScreen
import MoonshotApp.MokshaSetu.ui.screens.WishesScreen
import MoonshotApp.MokshaSetu.ui.screens.nominee.ClaimScreen
import MoonshotApp.MokshaSetu.ui.screens.nominee.DeathCertificateScreen
import MoonshotApp.MokshaSetu.ui.screens.nominee.EntitlementsScreen
import MoonshotApp.MokshaSetu.ui.screens.nominee.RegistryVerifyScreen
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy

sealed class Dest {
    data object RoleChooser : Dest()
    data class AadhaarLogin(val role: UserRole) : Dest()
    data object Discovery : Dest()
    data object PlannerHome : Dest()
    data object Assets : Dest()
    data object DigitalVault : Dest()
    data object Nominees : Dest()
    data object Wishes : Dest()
    data object PropertyUpload : Dest()
    data object DeathCertificate : Dest()
    data object RegistryVerify : Dest()
    data object Entitlements : Dest()
    data object Claim : Dest()
    data object SafetyNet : Dest()

    val isTabbed: Boolean get() = this in TAB_DESTS

    companion object {
        val TAB_DESTS = listOf(PlannerHome, Assets, DigitalVault, Nominees, Wishes)
    }
}

@Composable
fun VirasatApp() {
    val backStack = remember { mutableStateListOf<Dest>(Dest.RoleChooser) }
    val current = backStack.last()

    fun push(dest: Dest) = backStack.add(dest)

    fun pop() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    fun selectTab(dest: Dest) {
        backStack.clear()
        backStack.add(Dest.RoleChooser)
        backStack.add(Dest.PlannerHome)
        if (dest != Dest.PlannerHome) backStack.add(dest)
    }

    fun switchRole() {
        DemoRepository.resetDemo()
        backStack.clear()
        backStack.add(Dest.RoleChooser)
    }

    BackHandler(enabled = backStack.size > 1) { pop() }

    val switchLabel = stringResource(R.string.action_switch_role)
    val plannerFirstName = DemoRepository.plannerProfile?.name?.substringBefore(' ').orEmpty()

    Scaffold(
        topBar = {
            when (current) {
                Dest.Discovery -> VirasatTopBar(
                    stringResource(R.string.discovery_title),
                    stringResource(R.string.discovery_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                Dest.PlannerHome -> VirasatTopBar(
                    stringResource(R.string.home_title_fmt, plannerFirstName),
                    stringResource(R.string.home_subtitle),
                    showBack = false,
                    actionLabel = switchLabel,
                    onAction = ::switchRole
                )
                Dest.Assets -> VirasatTopBar(
                    stringResource(R.string.assets_title),
                    stringResource(R.string.assets_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                Dest.DigitalVault -> VirasatTopBar(
                    stringResource(R.string.digital_title),
                    stringResource(R.string.digital_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                Dest.Nominees -> VirasatTopBar(
                    stringResource(R.string.nominees_title),
                    stringResource(R.string.nominees_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                Dest.Wishes -> VirasatTopBar(
                    stringResource(R.string.wishes_title),
                    stringResource(R.string.wishes_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                Dest.PropertyUpload -> VirasatTopBar(
                    stringResource(R.string.property_title),
                    stringResource(R.string.property_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                Dest.DeathCertificate -> VirasatTopBar(
                    stringResource(R.string.cert_title),
                    stringResource(R.string.cert_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                Dest.RegistryVerify -> VirasatTopBar(
                    stringResource(R.string.registry_title),
                    stringResource(R.string.registry_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                Dest.Entitlements -> VirasatTopBar(
                    stringResource(R.string.entitlements_title),
                    stringResource(R.string.entitlements_subtitle),
                    showBack = true,
                    onBack = ::pop,
                    actionLabel = switchLabel,
                    onAction = ::switchRole
                )
                Dest.Claim -> VirasatTopBar(
                    stringResource(R.string.claim_title),
                    stringResource(R.string.claim_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                Dest.SafetyNet -> VirasatTopBar(
                    stringResource(R.string.safety_title),
                    stringResource(R.string.safety_subtitle),
                    showBack = true,
                    onBack = ::pop
                )
                else -> {}
            }
        },
        bottomBar = {
            if (current.isTabbed) {
                TabBar(current, ::selectTab)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding).background(MaterialTheme.colorScheme.background)) {
            when (val dest = current) {
                Dest.RoleChooser -> RoleChooserScreen(onChoose = { role ->
                    DemoRepository.enterRole(role)
                    push(Dest.AadhaarLogin(role))
                })
                is Dest.AadhaarLogin -> AadhaarLoginScreen(
                    role = dest.role,
                    onBack = ::pop,
                    onAuthenticated = {
                        if (dest.role == UserRole.PLANNER) push(Dest.Discovery) else push(Dest.DeathCertificate)
                    }
                )
                Dest.Discovery -> DiscoveryScreen(onContinue = { selectTab(Dest.PlannerHome) })
                Dest.PlannerHome -> HomeScreen(
                    onOpenAssets = { selectTab(Dest.Assets) },
                    onOpenDigital = { selectTab(Dest.DigitalVault) },
                    onOpenNominees = { selectTab(Dest.Nominees) },
                    onOpenWishes = { selectTab(Dest.Wishes) },
                    onOpenProperty = { push(Dest.PropertyUpload) }
                )
                Dest.Assets -> AssetsScreen(onAddProperty = { push(Dest.PropertyUpload) })
                Dest.DigitalVault -> DigitalVaultScreen()
                Dest.Nominees -> NomineesScreen()
                Dest.Wishes -> WishesScreen()
                Dest.PropertyUpload -> PropertyUploadScreen(onSaved = ::pop)
                Dest.DeathCertificate -> DeathCertificateScreen(onSubmit = { push(Dest.RegistryVerify) })
                Dest.RegistryVerify -> RegistryVerifyScreen(
                    onVerified = { push(Dest.Entitlements) },
                    onRetry = ::pop
                )
                Dest.Entitlements -> EntitlementsScreen(onClaim = { push(Dest.Claim) })
                Dest.Claim -> ClaimScreen(onOpenSafetyNet = { push(Dest.SafetyNet) })
                Dest.SafetyNet -> SafetyNetScreen(onRestart = ::switchRole)
            }
        }
    }
}

private data class TabSpec(val labelRes: Int, val emoji: String, val dest: Dest)

private val TAB_ITEMS = listOf(
    TabSpec(R.string.tab_home, "🏠", Dest.PlannerHome),
    TabSpec(R.string.tab_assets, "🏦", Dest.Assets),
    TabSpec(R.string.tab_digital, "🔐", Dest.DigitalVault),
    TabSpec(R.string.tab_nominees, "👥", Dest.Nominees),
    TabSpec(R.string.tab_wishes, "💌", Dest.Wishes)
)

@Composable
private fun TabBar(current: Dest, onSelect: (Dest) -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TAB_ITEMS.forEach { tab ->
                    val active = current == tab.dest
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .noRippleClickable { onSelect(tab.dest) }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(tab.emoji, fontSize = 17.sp)
                        Text(
                            stringResource(tab.labelRes),
                            fontSize = 9.5.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (active) Navy else Muted
                        )
                    }
                }
            }
            Row(Modifier.fillMaxWidth().height(1.dp).background(LineC)) {}
        }
    }
}
