package MoonshotApp.MokshaSetu.ui.screens.nominee

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.DigitalIdentity
import MoonshotApp.MokshaSetu.data.FinancialAsset
import MoonshotApp.MokshaSetu.data.PropertyDoc
import MoonshotApp.MokshaSetu.data.formatRupees
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.EmptyStateCard
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.MoneyCard
import MoonshotApp.MokshaSetu.ui.SecretRow
import MoonshotApp.MokshaSetu.ui.SectionTitle
import MoonshotApp.MokshaSetu.ui.StatusChip
import MoonshotApp.MokshaSetu.ui.emojiFor
import MoonshotApp.MokshaSetu.ui.labelFor
import MoonshotApp.MokshaSetu.ui.theme.AmberBg
import MoonshotApp.MokshaSetu.ui.theme.AmberText
import MoonshotApp.MokshaSetu.ui.theme.Cream
import MoonshotApp.MokshaSetu.ui.theme.GoldSoft
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Paper

@Composable
fun EntitlementsScreen(onClaim: () -> Unit) {
    val nominee = DemoRepository.activeNominee()
    val entitlements = nominee?.let { DemoRepository.entitlementsFor(it.id) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }

        if (nominee == null || entitlements == null || entitlements.isEmpty) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.entitlements_empty_title),
                    body = stringResource(R.string.entitlements_empty_body)
                )
            }
            return@LazyColumn
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Navy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.entitlements_total_label),
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp,
                        color = GoldSoft
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        formatRupees(entitlements.totalRupees),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(
                            R.string.entitlements_total_sub_fmt,
                            nominee.name,
                            nominee.relation
                        ),
                        fontSize = 10.5.sp,
                        color = GoldSoft
                    )
                }
            }
        }

        item { BannerCard(AmberBg, stringResource(R.string.entitlements_trustee_banner), AmberText) }

        item { SectionTitle(stringResource(R.string.entitlements_section_monetary)) }
        if (entitlements.assets.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.entitlements_no_money_title),
                    body = stringResource(R.string.entitlements_no_money_body)
                )
            }
        }
        items(entitlements.assets.size, key = { "ent-asset-${entitlements.assets[it].id}" }) { index ->
            MonetaryCard(entitlements.assets[index], nominee.id)
        }

        if (entitlements.propertyDocs.isNotEmpty()) {
            item { SectionTitle(stringResource(R.string.entitlements_section_papers)) }
            items(entitlements.propertyDocs.size, key = { "doc-${entitlements.propertyDocs[it].id}" }) { index ->
                PaperCard(entitlements.propertyDocs[index])
            }
        }

        item { SectionTitle(stringResource(R.string.entitlements_section_digital)) }
        if (entitlements.digitalIdentities.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.entitlements_no_digital_title),
                    body = stringResource(R.string.entitlements_no_digital_body)
                )
            }
        }
        items(
            entitlements.digitalIdentities.size,
            key = { "ent-digital-${entitlements.digitalIdentities[it].id}" }
        ) { index ->
            DigitalCard(entitlements.digitalIdentities[index])
        }

        item {
            Spacer(Modifier.height(2.dp))
            GoldButton(
                stringResource(R.string.entitlements_claim_cta),
                enabled = entitlements.assets.isNotEmpty(),
                onClick = onClaim
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MonetaryCard(asset: FinancialAsset, nomineeId: Int) {
    val split = asset.splitFor(nomineeId)
    MoneyCard(
        emoji = emojiFor(asset.kind),
        institution = asset.institution,
        maskedId = asset.maskedId,
        amount = formatRupees(asset.shareRupeesFor(nomineeId)),
        footer = if (split != null && split.percent < 100) {
            stringResource(
                R.string.entitlements_share_note_fmt,
                split.percent,
                formatRupees(asset.valueRupees)
            )
        } else {
            stringResource(R.string.entitlements_full_share)
        },
        chip = if (split != null && split.percent < 100) {
            stringResource(R.string.assets_share_fmt, split.percent) to ChipKind.AMBER
        } else {
            null
        }
    )
}

@Composable
private fun PaperCard(doc: PropertyDoc) {
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
            Box(
                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(Cream),
                contentAlignment = Alignment.Center
            ) { Text("📄", fontSize = 18.sp) }
            Column(Modifier.weight(1f)) {
                Text(doc.title, style = MaterialTheme.typography.titleMedium, color = Navy)
                Text(doc.fileName, style = MaterialTheme.typography.bodySmall, color = Muted)
            }
        }
    }
}

@Composable
private fun DigitalCard(identity: DigitalIdentity) {
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
                StatusChip(labelFor(identity.afterDeathAction), ChipKind.NAVY)
            }
            SecretRow(
                label = stringResource(R.string.digital_password_label),
                secret = identity.password,
                revealLabel = stringResource(R.string.digital_reveal),
                hideLabel = stringResource(R.string.digital_hide)
            )
            Surface(shape = RoundedCornerShape(10.dp), color = GreyBg, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.entitlements_wish_fmt, labelFor(identity.afterDeathAction)),
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    color = Muted,
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp)
                )
            }
        }
    }
}
