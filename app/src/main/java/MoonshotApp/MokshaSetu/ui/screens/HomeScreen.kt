package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.formatRupees
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.InfoCard
import MoonshotApp.MokshaSetu.ui.ScoreRing
import MoonshotApp.MokshaSetu.ui.SectionTitle
import MoonshotApp.MokshaSetu.ui.emojiFor
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.GreenBg
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Paper
import MoonshotApp.MokshaSetu.ui.theme.RedAlert

@Composable
fun HomeScreen(
    onOpenAssets: () -> Unit,
    onOpenDigital: () -> Unit,
    onOpenNominees: () -> Unit,
    onOpenWishes: () -> Unit,
    onOpenProperty: () -> Unit
) {
    val unassigned = DemoRepository.unassignedAssets()
    val unassignedDigital = DemoRepository.digitalIdentities.filter { it.nomineeId == null }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(Modifier.fillMaxWidth().padding(top = 18.dp), contentAlignment = Alignment.Center) {
                ScoreRing(DemoRepository.readinessScore(), stringResource(R.string.home_score_label))
            }
        }
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Paper,
                border = BorderStroke(1.dp, LineC),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        stringResource(R.string.home_mapped_label),
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp,
                        color = Muted
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        formatRupees(DemoRepository.totalMappedRupees()),
                        style = MaterialTheme.typography.displayLarge,
                        color = Navy
                    )
                    Text(
                        stringResource(
                            R.string.home_mapped_sub_fmt,
                            DemoRepository.assets.size,
                            DemoRepository.nominees.size
                        ),
                        fontSize = 10.5.sp,
                        color = Muted
                    )
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickTile(
                    "🏦",
                    stringResource(R.string.home_tile_assets),
                    stringResource(R.string.home_tile_assets_sub, DemoRepository.assets.size),
                    Modifier.weight(1f),
                    onOpenAssets
                )
                QuickTile(
                    "🔐",
                    stringResource(R.string.home_tile_digital),
                    stringResource(R.string.home_tile_digital_sub, DemoRepository.digitalIdentities.size),
                    Modifier.weight(1f),
                    onOpenDigital
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickTile(
                    "👥",
                    stringResource(R.string.home_tile_nominees),
                    stringResource(R.string.home_tile_nominees_sub, DemoRepository.nominees.size),
                    Modifier.weight(1f),
                    onOpenNominees
                )
                QuickTile(
                    "💌",
                    stringResource(R.string.home_tile_wishes),
                    stringResource(R.string.home_tile_wishes_sub, DemoRepository.wishes.size),
                    Modifier.weight(1f),
                    onOpenWishes
                )
            }
        }

        item { SectionTitle(stringResource(R.string.home_attention_title)) }

        if (unassigned.isEmpty() && unassignedDigital.isEmpty() && DemoRepository.propertyDocs.isNotEmpty()) {
            item {
                Surface(shape = RoundedCornerShape(14.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.home_all_clear),
                        fontSize = 11.5.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenOk,
                        modifier = Modifier.padding(13.dp)
                    )
                }
            }
        }

        items(unassigned, key = { "asset-${it.id}" }) { asset ->
            InfoCard(
                emoji = emojiFor(asset.kind),
                title = stringResource(R.string.home_attention_asset_fmt, asset.institution),
                body = stringResource(
                    R.string.home_attention_asset_body_fmt,
                    formatRupees(asset.valueRupees)
                ),
                chip = stringResource(R.string.chip_assign) to ChipKind.RED,
                accentBar = RedAlert,
                onClick = onOpenAssets
            )
        }

        items(unassignedDigital, key = { "digital-${it.id}" }) { identity ->
            InfoCard(
                emoji = identity.emoji,
                title = stringResource(R.string.home_attention_digital_fmt, identity.platform),
                body = stringResource(R.string.home_attention_digital_body),
                chip = stringResource(R.string.chip_assign) to ChipKind.RED,
                accentBar = RedAlert,
                onClick = onOpenDigital
            )
        }

        if (DemoRepository.propertyDocs.isEmpty()) {
            item {
                InfoCard(
                    emoji = "🏠",
                    title = stringResource(R.string.home_attention_property_title),
                    body = stringResource(R.string.home_attention_property_body),
                    chip = stringResource(R.string.chip_add) to ChipKind.AMBER,
                    onClick = onOpenProperty
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun QuickTile(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Paper,
        border = BorderStroke(1.dp, LineC),
        modifier = modifier.noRippleClickable(onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.height(5.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text(subtitle, fontSize = 10.sp, color = Muted)
        }
    }
}
