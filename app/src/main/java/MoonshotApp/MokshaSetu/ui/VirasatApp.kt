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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.ui.screens.ApproveScreen
import MoonshotApp.MokshaSetu.ui.screens.ClaimScreen
import MoonshotApp.MokshaSetu.ui.screens.CompartmentScreen
import MoonshotApp.MokshaSetu.ui.screens.DetectScreen
import MoonshotApp.MokshaSetu.ui.screens.HomeScreen
import MoonshotApp.MokshaSetu.ui.screens.LoginScreen
import MoonshotApp.MokshaSetu.ui.screens.NomineesScreen
import MoonshotApp.MokshaSetu.ui.screens.OutreachScreen
import MoonshotApp.MokshaSetu.ui.screens.SafetyNetScreen
import MoonshotApp.MokshaSetu.ui.screens.SaarthiScreen
import MoonshotApp.MokshaSetu.ui.screens.TriggersScreen
import MoonshotApp.MokshaSetu.ui.screens.UnlockScreen
import MoonshotApp.MokshaSetu.ui.screens.VaultScreen
import MoonshotApp.MokshaSetu.ui.screens.WishesScreen
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy

sealed class Dest {
    data object Login : Dest()
    data object Home : Dest()
    data object Vault : Dest()
    data object Nominees : Dest()
    data object Wishes : Dest()
    data object Saarthi : Dest()
    data object Claim : Dest()
    data object Triggers : Dest()
    data object Detect : Dest()
    data object Outreach : Dest()
    data object Unlock : Dest()
    data object Compartment : Dest()
    data object Approve : Dest()
    data object SafetyNet : Dest()

    val isTabbed: Boolean
        get() = this in TAB_DESTS

    companion object {
        val TAB_DESTS = listOf(Home, Vault, Wishes, Saarthi, Claim)
    }
}

@Composable
fun VirasatApp() {
    val backStack = remember { mutableStateListOf<Dest>(Dest.Login) }
    val current = backStack.last()

    fun push(dest: Dest) = backStack.add(dest)
    fun pop() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    BackHandler(enabled = backStack.size > 1) { pop() }

    Scaffold(
        topBar = {
            when (current) {
                Dest.Vault -> VirasatTopBar(stringResource(R.string.vault_title), stringResource(R.string.vault_subtitle), showBack = true, onBack = ::pop)
                Dest.Nominees -> VirasatTopBar(stringResource(R.string.nominees_title), stringResource(R.string.nominees_subtitle), showBack = true, onBack = ::pop)
                Dest.Wishes -> VirasatTopBar(stringResource(R.string.wishes_title), stringResource(R.string.wishes_subtitle), showBack = true, onBack = ::pop)
                Dest.Saarthi -> VirasatTopBar(stringResource(R.string.saarthi_title), stringResource(R.string.saarthi_subtitle), showBack = true, onBack = ::pop)
                Dest.Triggers -> VirasatTopBar(stringResource(R.string.triggers_title), stringResource(R.string.triggers_subtitle), showBack = true, onBack = ::pop)
                Dest.Detect -> VirasatTopBar(stringResource(R.string.detect_title), stringResource(R.string.detect_subtitle), showBack = true, onBack = ::pop)
                Dest.Unlock -> VirasatTopBar(stringResource(R.string.unlock_title), stringResource(R.string.unlock_subtitle), showBack = true, onBack = ::pop)
                Dest.Compartment -> VirasatTopBar(stringResource(R.string.compartment_title), stringResource(R.string.compartment_subtitle), showBack = false)
                Dest.Approve -> VirasatTopBar(stringResource(R.string.approve_title), stringResource(R.string.approve_subtitle), showBack = true, onBack = ::pop)
                Dest.SafetyNet -> VirasatTopBar(stringResource(R.string.safety_title), stringResource(R.string.safety_subtitle), showBack = true, onBack = ::pop)
                else -> {}
            }
        },
        bottomBar = {
            if (current.isTabbed) {
                TabBar(current) { dest ->
                    while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    backStack.add(dest)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding).background(MaterialTheme.colorScheme.background)) {
            when (current) {
                Dest.Login -> LoginScreen(onEnterPlanner = { push(Dest.Home) })
                Dest.Home -> HomeScreen(
                    onOpenVault = { push(Dest.Vault) },
                    onOpenNominees = { push(Dest.Nominees) },
                    onOpenWishes = { push(Dest.Wishes) },
                    onOpenSaarthi = { push(Dest.Saarthi) },
                    onOpenTriggers = { push(Dest.Triggers) },
                    onPreviewNomineeJourney = { push(Dest.Detect) }
                )
                Dest.Vault -> VaultScreen()
                Dest.Nominees -> NomineesScreen()
                Dest.Wishes -> WishesScreen()
                Dest.Saarthi -> SaarthiScreen()
                Dest.Claim -> ClaimScreen(onOpenSafetyNet = { push(Dest.SafetyNet) })
                Dest.Triggers -> TriggersScreen()
                Dest.Detect -> DetectScreen(onBegin = { push(Dest.Outreach) })
                Dest.Outreach -> OutreachScreen(onOpenVaultOfFather = { push(Dest.Unlock) })
                Dest.Unlock -> UnlockScreen(onUnlocked = { push(Dest.Compartment) })
                Dest.Compartment -> CompartmentScreen(popBack = ::pop, onBeginSettlement = { push(Dest.Approve) })
                Dest.Approve -> ApproveScreen(onSeeSettlement = { push(Dest.Claim) })
                Dest.SafetyNet -> SafetyNetScreen(onRestart = {
                    DemoRepository.nomineeUnlocked.value = false
                    while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    backStack.add(Dest.Login)
                })
            }
        }
    }
}

private val TAB_ITEMS = listOf(
    TabSpec(R.string.tab_home, "🏠", Dest.Home),
    TabSpec(R.string.tab_vault, "🗄️", Dest.Vault),
    TabSpec(R.string.tab_wishes, "💌", Dest.Wishes),
    TabSpec(R.string.tab_saarthi, "🪔", Dest.Saarthi),
    TabSpec(R.string.tab_claim, "🕊️", Dest.Claim)
)

private data class TabSpec(val labelRes: Int, val emoji: String, val dest: Dest)

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
