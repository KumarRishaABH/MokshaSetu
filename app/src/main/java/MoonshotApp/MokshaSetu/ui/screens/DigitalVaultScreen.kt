package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.DigitalAction
import MoonshotApp.MokshaSetu.data.DigitalIdentity
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.DashedActionCard
import MoonshotApp.MokshaSetu.ui.EmptyStateCard
import MoonshotApp.MokshaSetu.ui.SecretRow
import MoonshotApp.MokshaSetu.ui.StatusChip
import MoonshotApp.MokshaSetu.ui.VirasatTextField
import MoonshotApp.MokshaSetu.ui.labelFor
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.Cream
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Paper
import MoonshotApp.MokshaSetu.ui.theme.RedAlert

@Composable
fun DigitalVaultScreen() {
    var showAdd by remember { mutableStateOf(false) }
    var picking by remember { mutableStateOf<DigitalIdentity?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { BannerCard(GreyBg, stringResource(R.string.digital_banner)) }
        if (DemoRepository.digitalIdentities.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.digital_empty_title),
                    body = stringResource(R.string.digital_empty_body)
                )
            }
        }
        items(DemoRepository.digitalIdentities, key = { it.id }) { identity ->
            IdentityCard(identity, onAssign = { picking = identity })
        }
        item {
            DashedActionCard(
                stringResource(R.string.digital_add_cta),
                stringResource(R.string.digital_add_sub),
                onClick = { showAdd = true }
            )
            Spacer(Modifier.height(14.dp))
        }
    }

    if (showAdd) {
        AddIdentityDialog(onDismiss = { showAdd = false })
    }

    picking?.let { identity ->
        NomineePickerDialog(
            title = stringResource(R.string.picker_title_digital_fmt, identity.platform),
            selected = identity.nomineeId,
            onDismiss = { picking = null },
            onPick = { nomineeId ->
                DemoRepository.assignDigitalNominee(identity.id, nomineeId)
                picking = null
            }
        )
    }
}

@Composable
private fun IdentityCard(identity: DigitalIdentity, onAssign: () -> Unit) {
    val nominee = DemoRepository.nomineeById(identity.nomineeId)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Paper,
        border = BorderStroke(1.dp, LineC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Box(
                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(Cream),
                    contentAlignment = Alignment.Center
                ) { Text(identity.emoji, fontSize = 18.sp) }
                Column(Modifier.weight(1f)) {
                    Text(identity.platform, style = MaterialTheme.typography.titleMedium, color = Navy)
                    Text(identity.username, style = MaterialTheme.typography.bodySmall, color = Muted)
                }
                StatusChip(labelFor(identity.afterDeathAction), chipKindFor(identity.afterDeathAction))
            }

            SecretRow(
                label = stringResource(R.string.digital_password_label),
                secret = identity.password,
                revealLabel = stringResource(R.string.digital_reveal),
                hideLabel = stringResource(R.string.digital_hide)
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GreyBg,
                modifier = Modifier.fillMaxWidth().noRippleClickable(onAssign)
            ) {
                Text(
                    if (nominee == null) {
                        stringResource(R.string.digital_no_nominee)
                    } else {
                        stringResource(R.string.digital_nominee_fmt, nominee.name, nominee.relation)
                    },
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (nominee == null) RedAlert else Navy,
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp)
                )
            }
        }
    }
}

private fun chipKindFor(action: DigitalAction): ChipKind = when (action) {
    DigitalAction.MEMORIALISE -> ChipKind.NAVY
    DigitalAction.DELETE -> ChipKind.RED
    DigitalAction.TRANSFER_ACCESS -> ChipKind.GREEN
}

@Composable
private fun AddIdentityDialog(onDismiss: () -> Unit) {
    var platform by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var action by remember { mutableStateOf(DigitalAction.MEMORIALISE) }
    var nomineeId by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.digital_add_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                VirasatTextField(
                    value = platform,
                    onValueChange = { platform = it },
                    label = stringResource(R.string.digital_platform_label)
                )
                VirasatTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = stringResource(R.string.digital_username_label)
                )
                VirasatTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.digital_password_field_label)
                )
                Text(
                    stringResource(R.string.digital_action_label),
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DigitalAction.entries.forEach { option ->
                        ChoiceChip(
                            text = labelFor(option),
                            active = action == option,
                            modifier = Modifier.weight(1f),
                            onClick = { action = option }
                        )
                    }
                }
                Text(
                    stringResource(R.string.digital_nominee_label),
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DemoRepository.nominees.forEach { nominee ->
                        ChoiceChip(
                            text = nominee.name.substringBefore(' '),
                            active = nomineeId == nominee.id,
                            modifier = Modifier.weight(1f),
                            onClick = { nomineeId = if (nomineeId == nominee.id) null else nominee.id }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (platform.isNotBlank() && username.isNotBlank()) {
                    DemoRepository.addDigitalIdentity(
                        platform = platform.trim(),
                        username = username.trim(),
                        password = password.trim(),
                        nomineeId = nomineeId,
                        action = action
                    )
                }
                onDismiss()
            }) { Text(stringResource(R.string.digital_add_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_cancel)) }
        }
    )
}

@Composable
private fun ChoiceChip(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (active) Gold else GreyBg,
        modifier = modifier.noRippleClickable(onClick)
    ) {
        Text(
            text,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) Navy else Muted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp)
        )
    }
}
