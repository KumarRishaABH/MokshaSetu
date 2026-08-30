package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.AssetKind
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.FinancialAsset
import MoonshotApp.MokshaSetu.data.formatRupees
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.DashedActionCard
import MoonshotApp.MokshaSetu.ui.EmptyStateCard
import MoonshotApp.MokshaSetu.ui.MoneyCard
import MoonshotApp.MokshaSetu.ui.SectionTitle
import MoonshotApp.MokshaSetu.ui.emojiFor
import MoonshotApp.MokshaSetu.ui.sectionLabelFor
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.RedAlert

@Composable
fun AssetsScreen(onAddProperty: () -> Unit) {
    var picking by remember { mutableStateOf<FinancialAsset?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { BannerCard(GreyBg, stringResource(R.string.assets_banner)) }

        AssetKind.entries.forEach { kind ->
            val inKind = DemoRepository.assets.filter { it.kind == kind }
            item(key = "title-$kind") {
                SectionTitle(
                    stringResource(
                        R.string.assets_section_fmt,
                        sectionLabelFor(kind),
                        formatRupees(inKind.sumOf { it.valueRupees })
                    )
                )
            }
            if (inKind.isEmpty()) {
                item(key = "empty-$kind") {
                    EmptyStateCard(
                        title = stringResource(R.string.assets_empty_title),
                        body = stringResource(R.string.assets_empty_body_fmt, sectionLabelFor(kind))
                    )
                }
            }
            items(inKind.size, key = { index -> "asset-${inKind[index].id}" }) { index ->
                AssetCard(inKind[index], onAssign = { picking = inKind[index] })
            }
        }

        item {
            DashedActionCard(
                stringResource(R.string.assets_add_property_cta),
                stringResource(R.string.assets_add_property_sub),
                onClick = onAddProperty
            )
            Spacer(Modifier.height(14.dp))
        }
    }

    picking?.let { asset ->
        ShareSplitDialog(
            title = stringResource(R.string.picker_title_asset_fmt, asset.institution),
            asset = asset,
            onDismiss = { picking = null },
            onSave = { splits ->
                DemoRepository.assignAssetSplits(asset.id, splits)
                picking = null
            }
        )
    }
}

@Composable
private fun AssetCard(asset: FinancialAsset, onAssign: () -> Unit) {
    val splits = asset.splits.mapNotNull { split ->
        DemoRepository.nomineeById(split.nomineeId)?.let { split to it }
    }
    val doc = DemoRepository.propertyDocFor(asset.id)

    MoneyCard(
        emoji = emojiFor(asset.kind),
        institution = asset.institution,
        maskedId = asset.maskedId,
        amount = formatRupees(asset.valueRupees),
        footer = "",
        chip = null,
        accentBar = if (splits.isEmpty()) RedAlert else null,
        onClick = onAssign,
        trailing = {
            if (splits.isEmpty()) {
                Text(
                    stringResource(R.string.asset_no_nominee_tap),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedAlert
                )
            } else {
                splits.forEach { (split, nominee) ->
                    Text(
                        stringResource(R.string.asset_split_line_fmt, nominee.name, split.percent),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Muted
                    )
                }
                if (asset.unregisteredPercent > 0) {
                    Text(
                        stringResource(R.string.asset_unregistered_fmt, asset.unregisteredPercent),
                        fontSize = 10.sp,
                        color = RedAlert
                    )
                }
            }
            if (doc != null) {
                Text(
                    stringResource(R.string.assets_doc_fmt, doc.fileName),
                    fontSize = 10.sp,
                    color = Muted
                )
            }
            Text(
                stringResource(R.string.discovery_via_fmt, asset.discoveredVia),
                fontSize = 9.5.sp,
                color = Muted
            )
        }
    )
}
