package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.Nominee
import MoonshotApp.MokshaSetu.data.formatRupees
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.DashedActionCard
import MoonshotApp.MokshaSetu.ui.SectionTitle
import MoonshotApp.MokshaSetu.ui.StatusChip
import MoonshotApp.MokshaSetu.ui.VirasatTextField
import MoonshotApp.MokshaSetu.ui.emojiFor
import MoonshotApp.MokshaSetu.ui.labelFor
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.AmberBg
import MoonshotApp.MokshaSetu.ui.theme.AmberText
import MoonshotApp.MokshaSetu.ui.theme.Cream
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Paper
import MoonshotApp.MokshaSetu.ui.theme.RedAlert
import MoonshotApp.MokshaSetu.ui.theme.RedBg

@Composable
fun NomineesScreen() {
    var showAdd by remember { mutableStateOf(false) }
    val unassigned = DemoRepository.unassignedAssets()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { SectionTitle(stringResource(R.string.nominees_section_people)) }
        items(DemoRepository.nominees, key = { it.id }) { nominee -> NomineeCard(nominee) }
        item {
            DashedActionCard(
                stringResource(R.string.nominees_add_cta),
                stringResource(R.string.nominees_add_sub),
                onClick = { showAdd = true }
            )
        }

        if (unassigned.isNotEmpty()) {
            item { SectionTitle(stringResource(R.string.nominees_section_unassigned)) }
            item {
                Surface(shape = RoundedCornerShape(14.dp), color = RedBg, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(
                            pluralStringResource(R.plurals.nominees_unassigned_title, unassigned.size, unassigned.size),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedAlert
                        )
                        unassigned.forEach { asset ->
                            Text(
                                stringResource(
                                    R.string.nominees_unassigned_row_fmt,
                                    emojiFor(asset.kind),
                                    asset.institution,
                                    formatRupees(asset.valueRupees)
                                ),
                                fontSize = 11.sp,
                                color = Navy
                            )
                        }
                        Text(
                            stringResource(R.string.nominees_unassigned_note),
                            fontSize = 10.sp,
                            lineHeight = 15.sp,
                            color = Muted
                        )
                    }
                }
            }
        }

        item { BannerCard(AmberBg, stringResource(R.string.nominees_dpdp_banner), AmberText) }
        item { Spacer(Modifier.height(12.dp)) }
    }

    if (showAdd) {
        AddNomineeDialog(onDismiss = { showAdd = false })
    }
}

@Composable
private fun NomineeCard(nominee: Nominee) {
    var expanded by remember { mutableStateOf(false) }
    val assets = DemoRepository.assetsOf(nominee.id)
    val identities = DemoRepository.digitalIdentitiesOf(nominee.id)
    val total = assets.sumOf { it.nomineeShareRupees }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Paper,
        border = BorderStroke(1.dp, LineC),
        modifier = Modifier.fillMaxWidth().noRippleClickable { expanded = !expanded }
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Gold)
                ) {
                    Text(nominee.initial, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text(nominee.name, style = MaterialTheme.typography.titleMedium, color = Navy)
                    Text(
                        stringResource(
                            R.string.nominees_person_sub_fmt,
                            nominee.relation,
                            nominee.maskedAadhaar
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted
                    )
                }
                StatusChip(
                    if (nominee.verified) {
                        stringResource(R.string.chip_verified)
                    } else {
                        stringResource(R.string.chip_invited)
                    },
                    if (nominee.verified) ChipKind.GREEN else ChipKind.AMBER
                )
            }

            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.nominees_total_label),
                        fontSize = 9.5.sp,
                        letterSpacing = 0.8.sp,
                        color = Muted
                    )
                    Text(
                        formatRupees(total),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Navy
                    )
                }
                Text(
                    stringResource(
                        R.string.nominees_counts_join_fmt,
                        pluralStringResource(R.plurals.nominees_assets_count, assets.size, assets.size),
                        pluralStringResource(R.plurals.nominees_accounts_count, identities.size, identities.size)
                    ),
                    fontSize = 10.5.sp,
                    color = Muted
                )
                Text(
                    if (expanded) " ▲" else " ▼",
                    fontSize = 10.sp,
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (assets.isEmpty() && identities.isEmpty()) {
                        Text(
                            stringResource(R.string.nominees_nothing_assigned),
                            fontSize = 11.sp,
                            color = Muted
                        )
                    }
                    assets.forEach { asset ->
                        DetailRow(
                            emoji = emojiFor(asset.kind),
                            title = asset.institution,
                            subtitle = if (asset.sharePercent < 100) {
                                stringResource(
                                    R.string.nominees_row_share_fmt,
                                    asset.maskedId,
                                    asset.sharePercent
                                )
                            } else {
                                asset.maskedId
                            },
                            trailing = formatRupees(asset.nomineeShareRupees)
                        )
                    }
                    identities.forEach { identity ->
                        DetailRow(
                            emoji = identity.emoji,
                            title = identity.platform,
                            subtitle = identity.username,
                            trailing = labelFor(identity.afterDeathAction)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(emoji: String, title: String, subtitle: String, trailing: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Cream)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(emoji, fontSize = 14.sp)
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Navy)
            Text(subtitle, fontSize = 9.5.sp, color = Muted)
        }
        Text(trailing, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Navy)
    }
}

@Composable
private fun AddNomineeDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    var aadhaar by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.nominees_add_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                VirasatTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.nominees_name_label)
                )
                VirasatTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = stringResource(R.string.nominees_relation_label)
                )
                VirasatTextField(
                    value = aadhaar,
                    onValueChange = { aadhaar = it.filter { char -> char.isDigit() }.take(12) },
                    label = stringResource(R.string.nominees_aadhaar_label),
                    keyboardType = KeyboardType.NumberPassword,
                    supportingText = stringResource(R.string.nominees_aadhaar_help)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank() && relation.isNotBlank()) {
                    DemoRepository.addNominee(name.trim(), relation.trim(), aadhaar)
                }
                onDismiss()
            }) { Text(stringResource(R.string.nominees_add_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_cancel)) }
        }
    )
}
