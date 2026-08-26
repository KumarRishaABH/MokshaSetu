package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.Tier2Action
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.SectionTitle
import MoonshotApp.MokshaSetu.ui.StatusChip
import MoonshotApp.MokshaSetu.ui.theme.AmberBg
import MoonshotApp.MokshaSetu.ui.theme.AmberText
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.RedAlert

@Composable
fun TriggersScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.tier1_header), style = MaterialTheme.typography.titleLarge)
                StatusChip(stringResource(R.string.tier1_chip), ChipKind.GREEN)
            }
        }
        items(DemoRepository.tier1Actions) { action ->
            Tier1Row(action.id, stringResource(action.labelRes), action.armed)
        }
        item { BannerNote(stringResource(R.string.tier1_note)) }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.tier2_header), style = MaterialTheme.typography.titleLarge)
                StatusChip(stringResource(R.string.tier2_chip), ChipKind.RED)
            }
        }
        items(DemoRepository.tier2Actions) { action ->
            Tier2Card(action)
        }
        item {
            BannerNote(stringResource(R.string.tier2_note))
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun Tier1Row(id: Int, label: String, armed: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, LineC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp).fillMaxWidth()
        ) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Switch(
                checked = armed,
                onCheckedChange = { DemoRepository.toggleTier1(id) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = GreenOk,
                    uncheckedTrackColor = LineC,
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun Tier2Card(action: Tier2Action) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (action.armed) GreenOk else LineC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(action.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (action.armed) StatusChip(stringResource(R.string.chip_armed), ChipKind.GREEN)
            }
            RequirementRow(stringResource(R.string.req_cert), action.certVerified)
            RequirementRow(stringResource(R.string.req_aadhaar), action.aadhaarVerified)
            RequirementRow(stringResource(R.string.req_coauth), action.coAuthDone)
            RequirementRow(stringResource(R.string.req_wait), action.waitingDays >= 21)

            when {
                !action.coAuthDone -> GoldButton(stringResource(R.string.tier2_coauth_cta)) {
                    DemoRepository.requestCoAuth(action.id)
                }
                action.waitingDays < 21 -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        stringResource(R.string.tier2_waiting_fmt, action.waitingDays),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberText
                    )
                    GoldButton(stringResource(R.string.tier2_coauth_done) + " · +7d") {
                        DemoRepository.advanceWaiting(action.id)
                    }
                }
                else -> GoldButton(
                    stringResource(R.string.tier2_arm_cta),
                    enabled = !action.armed
                ) { DemoRepository.armTier2(action.id) }
            }
        }
    }
}

@Composable
private fun RequirementRow(label: String, done: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(if (done) "✅" else "⬜", fontSize = 12.sp)
        Text(label, fontSize = 12.sp, color = if (done) GreenOk else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BannerNote(text: String) {
    Surface(shape = MaterialTheme.shapes.medium, color = AmberBg) {
        Text(
            text,
            color = AmberText,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(12.dp).fillMaxWidth()
        )
    }
}
