package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.InfoCard
import MoonshotApp.MokshaSetu.ui.StatusChip
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.AmberBg
import MoonshotApp.MokshaSetu.ui.theme.AmberText
import MoonshotApp.MokshaSetu.ui.theme.GreenBg
import MoonshotApp.MokshaSetu.ui.theme.GoldSoft
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Paper
import MoonshotApp.MokshaSetu.ui.theme.RedAlert
import MoonshotApp.MokshaSetu.ui.theme.WaAvatar
import MoonshotApp.MokshaSetu.ui.theme.WaBody
import MoonshotApp.MokshaSetu.ui.theme.WaHead
import MoonshotApp.MokshaSetu.ui.theme.WaInfoBg
import MoonshotApp.MokshaSetu.ui.theme.WaNoteBg
import MoonshotApp.MokshaSetu.ui.theme.WaOut
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DetectScreen(onBegin: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item {
            Text(
                stringResource(R.string.detect_intro),
                fontSize = 11.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            InfoCard("🟡", stringResource(R.string.detect_l1_title), stringResource(R.string.detect_l1_trigger), accentBar = Color(0xFFC9A24B))
            BodyText(stringResource(R.string.detect_l1_body))
        }
        item {
            InfoCard("🟠", stringResource(R.string.detect_l2_title), stringResource(R.string.detect_l2_trigger), accentBar = Color(0xFFE08A2B))
            BodyText(stringResource(R.string.detect_l2_body))
        }
        item {
            InfoCard("🟢", stringResource(R.string.detect_l3_title), stringResource(R.string.detect_l3_trigger), accentBar = GreenOk)
            BodyText(stringResource(R.string.detect_l3_body))
        }
        item {
            Spacer(Modifier.height(6.dp))
            GoldButton(stringResource(R.string.detect_begin_cta), onClick = onBegin)
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun BodyText(text: String) {
    Text(
        text,
        fontSize = 10.5.sp,
        lineHeight = 16.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 6.dp).fillMaxWidth()
    )
}

@Composable
fun OutreachScreen(onOpenVaultOfFather: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(WaBody)) {
        Surface(color = WaHead) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(CircleShape).background(WaAvatar)) {
                    Text("🪔", fontSize = 17.sp)
                }
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.wa_saarthi_name), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(stringResource(R.string.wa_saarthi_status), fontSize = 10.sp, color = Color(0xFFA7CCC5))
                }
                Text("📞", fontSize = 15.sp)
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f).padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            item { CenterNote(WaNoteBg, stringResource(R.string.wa_trigger_notice)) }
            item { CenterNote(WaInfoBg, stringResource(R.string.wa_privacy_notice)) }
            item { WaBubble(stringResource(R.string.wa_in_1), R.string.wa_time_1, incoming = true) }
            item { WaBubble(stringResource(R.string.wa_in_2), null, incoming = true) }
            item { WaBubble(stringResource(R.string.wa_in_3), null, incoming = true) }
            item { WaBubble(stringResource(R.string.wa_out_1), R.string.wa_time_2, incoming = false) }
            item { WaBubble(stringResource(R.string.wa_in_4), null, incoming = true) }
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Navy,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).noRippleClickable(onOpenVaultOfFather)
                ) {
                    Text(
                        stringResource(R.string.wa_open_cta),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(11.dp)
                    )
                }
            }
            item { CenterNote(Color.Transparent, stringResource(R.string.wa_footer)) }
        }
    }
}

@Composable
private fun CenterNote(bg: Color, text: String) {
    Surface(shape = RoundedCornerShape(6.dp), color = bg, modifier = Modifier.fillMaxWidth()) {
        Text(
            text,
            fontSize = 9.sp,
            lineHeight = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(7.dp).fillMaxWidth()
        )
    }
}

@Composable
private fun WaBubble(text: String, timeRes: Int?, incoming: Boolean) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (incoming) Alignment.CenterStart else Alignment.CenterEnd) {
        Surface(shape = RoundedCornerShape(10.dp), color = if (incoming) Paper else WaOut, modifier = Modifier.fillMaxWidth(0.84f)) {
            Column(Modifier.padding(horizontal = 11.dp, vertical = 8.dp)) {
                Text(text, fontSize = 12.sp, lineHeight = 17.sp, color = Color(0xFF132038))
                timeRes?.let {
                    Text(stringResource(it), fontSize = 8.sp, color = Color(0xFF8FA39F), textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

private data class ShareUi(val dot: String, val titleRes: Int, val subRes: Int, val lit: Boolean?, val unused: Boolean)

@Composable
fun UnlockScreen(onUnlocked: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    var result by remember { mutableStateOf<Boolean?>(null) }
    val scope = rememberCoroutineScope()

    fun shareUi(dot: String, titleRes: Int, subRes: Int, position: Int): ShareUi =
        ShareUi(dot, titleRes, subRes, lit = step >= position, unused = false)

    val rowOne = listOf(
        ShareUi("✕", R.string.share_holder_name, R.string.share_holder_sub, lit = null, unused = true),
        shareUi("1", R.string.share_rohan_name, R.string.share_rohan_sub, 1)
    )
    val rowTwo = listOf(
        shareUi("2", R.string.share_enclave_name, R.string.share_enclave_sub, 2),
        shareUi("3", R.string.share_escrow_name, R.string.share_escrow_sub, 3)
    )
    val rowThree = listOf(
        ShareUi("–", R.string.share_meera_name, R.string.share_meera_sub, lit = null, unused = true)
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        Text("🗝️", fontSize = 34.sp)
        Text(stringResource(R.string.unlock_hero_title), style = MaterialTheme.typography.titleLarge)
        Text(
            stringResource(R.string.unlock_hero_body),
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.unlock_meter_fmt, minOf(step, DemoRepository.THRESHOLD)),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Navy,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(9.dp).fillMaxWidth()
            )
        }
        ShareRow(rowOne)
        ShareRow(rowTwo)
        ShareRow(rowThree)

        when (result) {
            null -> GoldButton(stringResource(R.string.unlock_auth_cta), enabled = step == 0) {
                scope.launch {
                    step = 1; delay(600)
                    step = 2; delay(600)
                    step = 3; delay(500)
                    result = DemoRepository.attemptUnlock()
                }
            }
            true -> {
                Surface(shape = RoundedCornerShape(12.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.unlock_success),
                        color = GreenOk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp).fillMaxWidth()
                    )
                }
                GoldButton(stringResource(R.string.unlock_see_cta), onClick = onUnlocked)
            }
            false -> Text(stringResource(R.string.unlock_fail), color = RedAlert, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.weight(1f))
        Text(
            stringResource(R.string.unlock_footnote),
            fontSize = 9.5.sp,
            lineHeight = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Surface(shape = RoundedCornerShape(10.dp), color = GreyBg, modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.unlock_cert_note),
                fontSize = 9.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(10.dp).fillMaxWidth()
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ShareRow(shares: List<ShareUi>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        shares.forEach { share ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Paper,
                border = BorderStroke(1.dp, if (share.lit == true) GreenOk else LineC),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(9.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(if (share.lit == true) GreenOk else if (share.unused) Color(0xFFE3B7B7) else Color(0xFFCFD4E0))
                    ) {
                        Text(share.dot, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(stringResource(share.titleRes), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, lineHeight = 13.sp)
                        Text(stringResource(share.subRes), fontSize = 8.5.sp, lineHeight = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun CompartmentScreen(popBack: () -> Unit, onBeginSettlement: () -> Unit) {
    if (!DemoRepository.nomineeUnlocked.value) {
        LaunchedEffect(Unit) { popBack() }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { BannerCard(AmberBg, stringResource(R.string.compartment_scope_banner), AmberText) }
        item { SectionTitleLocal(stringResource(R.string.compartment_section_title)) }
        item { InfoCard("🏦", "HDFC ••4021", stringResource(R.string.comp_hdfc_sub), chip = stringResource(R.string.chip_found) to ChipKind.GREEN) }
        item { InfoCard("🏦", stringResource(R.string.comp_sbi_title), stringResource(R.string.comp_sbi_sub), chip = stringResource(R.string.chip_new) to ChipKind.AMBER) }
        item { InfoCard("💤", stringResource(R.string.comp_dormant_title), stringResource(R.string.comp_dormant_sub), chip = stringResource(R.string.chip_new) to ChipKind.AMBER) }
        item { InfoCard("🛡️", stringResource(R.string.comp_lic_title), stringResource(R.string.comp_lic_sub), chip = stringResource(R.string.chip_ready) to ChipKind.GREEN) }
        item { DecryptedMessageCard(DemoRepository.decryptedMessage) }
        item {
            GoldButton(stringResource(R.string.comp_settle_cta), onClick = onBeginSettlement)
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun SectionTitleLocal(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun DecryptedMessageCard(message: String?) {
    Surface(
        shape = RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp, bottomStart = 14.dp),
        color = Paper,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(stringResource(R.string.comp_message_label), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            if (message != null) {
                Text(
                    "\"$message\"",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Navy
                )
            } else {
                Text("…", fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontSize = 13.sp, color = Navy)
            }
        }
    }
}

@Composable
fun ApproveScreen(onSeeSettlement: () -> Unit) {
    var meeraApproved by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { InfoCard("🔐", "Joint Fixed Deposit · ₹18,00,000", stringResource(R.string.approve_fd_sub)) }
        item { SectionTitleLocal(stringResource(R.string.approve_status_title)) }
        item {
            ApprovalPerson(initial = "R", name = stringResource(R.string.approve_you_name), sub = stringResource(R.string.approve_you_sub), chip = stringResource(R.string.chip_done) to ChipKind.GREEN)
        }
        item {
            ApprovalPerson(
                initial = "M",
                name = stringResource(R.string.approve_meera_name),
                sub = stringResource(R.string.approve_meera_sub),
                chip = (if (meeraApproved) stringResource(R.string.chip_done) else stringResource(R.string.chip_waiting)) to
                    (if (meeraApproved) ChipKind.GREEN else ChipKind.AMBER)
            )
        }
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Paper,
                border = BorderStroke(1.dp, LineC),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.approve_preview_label), fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = Navy, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(stringResource(R.string.approve_notif_app), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldSoft)
                            Spacer(Modifier.height(4.dp))
                            Text(stringResource(R.string.approve_notif_body), fontSize = 11.sp, lineHeight = 16.sp, color = Color.White)
                            Spacer(Modifier.height(10.dp))
                            if (!meeraApproved) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.noRippleClickable { meeraApproved = true }
                                ) {
                                    Text(
                                        stringResource(R.string.approve_notif_btn),
                                        color = Navy,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            } else {
                                StatusChip(stringResource(R.string.chip_done), ChipKind.GREEN)
                            }
                        }
                    }
                    if (meeraApproved) {
                        Spacer(Modifier.height(10.dp))
                        Surface(shape = RoundedCornerShape(12.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.approve_confirmed),
                                color = GreenOk,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp).fillMaxWidth()
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        GoldButton(stringResource(R.string.approve_settlement_cta), onClick = onSeeSettlement)
                    }
                }
            }
        }
        item { BannerCard(GreyBg, stringResource(R.string.approve_fallback_note)) }
        item {
            Text(
                stringResource(R.string.approve_footer),
                fontSize = 9.5.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
            )
        }
    }
}

@Composable
private fun ApprovalPerson(initial: String, name: String, sub: String, chip: Pair<String, ChipKind>) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Paper,
        border = BorderStroke(1.dp, LineC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            modifier = Modifier.padding(14.dp).fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary)) {
                Text(initial, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusChip(chip.first, chip.second)
        }
    }
}

@Composable
fun SafetyNetScreen(onRestart: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { BannerCard(GreyBg, stringResource(R.string.safety_intro)) }
        item { InfoCard(null, stringResource(R.string.safety_c1_title), stringResource(R.string.safety_c1_body)) }
        item { InfoCard(null, stringResource(R.string.safety_c2_title), stringResource(R.string.safety_c2_body)) }
        item { InfoCard(null, stringResource(R.string.safety_c3_title), stringResource(R.string.safety_c3_body)) }
        item { InfoCard(null, stringResource(R.string.safety_c4_title), stringResource(R.string.safety_c4_body)) }
        item { InfoCard(null, stringResource(R.string.safety_c5_title), stringResource(R.string.safety_c5_body)) }
        item { BannerCard(AmberBg, stringResource(R.string.safety_trustee_note), AmberText) }
        item {
            Spacer(Modifier.height(4.dp))
            GoldButton(stringResource(R.string.safety_restart_cta), onClick = onRestart)
            Spacer(Modifier.height(14.dp))
        }
    }
}
