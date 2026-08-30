package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.FinancialAsset
import MoonshotApp.MokshaSetu.data.formatRupees
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.MoneyCard
import MoonshotApp.MokshaSetu.ui.emojiFor
import MoonshotApp.MokshaSetu.ui.theme.AmberBg
import MoonshotApp.MokshaSetu.ui.theme.AmberText
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.GreenBg
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.RedAlert

@Composable
fun DiscoveryScreen(onContinue: () -> Unit) {
    val expected = DemoRepository.accountDiscovery.expectedCount
    var done by remember { mutableStateOf(DemoRepository.assets.size >= expected) }

    LaunchedEffect(Unit) {
        val profile = DemoRepository.plannerProfile
        if (!done && profile != null) {
            DemoRepository.accountDiscovery.discover(profile) { DemoRepository.onAssetDiscovered(it) }
        }
        done = true
    }

    val discovered = DemoRepository.assets.toList()
    val total = discovered.sumOf { it.valueRupees }
    val unassigned = discovered.count { !it.isAssigned }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(6.dp))

        Surface(shape = RoundedCornerShape(14.dp), color = GreyBg, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(13.dp)) {
                Text(
                    stringResource(R.string.discovery_consent_title),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.discovery_consent_body),
                    fontSize = 10.5.sp,
                    lineHeight = 16.sp,
                    color = Muted
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!done) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Gold, strokeWidth = 2.dp)
            } else {
                Text("✓", color = GreenOk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f)) {
                Text(
                    if (done) {
                        stringResource(R.string.discovery_done_caption)
                    } else {
                        stringResource(R.string.discovery_fetching)
                    },
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Navy
                )
                Text(
                    stringResource(R.string.discovery_count_fmt, discovered.size, expected),
                    fontSize = 10.sp,
                    color = Muted
                )
            }
            Text(
                formatRupees(total),
                style = MaterialTheme.typography.titleLarge,
                color = Navy
            )
        }

        discovered.forEach { asset -> DiscoveredCard(asset) }

        if (done) {
            Surface(shape = RoundedCornerShape(14.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(13.dp)) {
                    Text(
                        stringResource(R.string.discovery_summary_title_fmt, discovered.size),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenOk
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.discovery_summary_body_fmt, formatRupees(total)),
                        fontSize = 10.5.sp,
                        lineHeight = 16.sp,
                        color = Navy
                    )
                }
            }
            if (unassigned > 0) {
                Surface(shape = RoundedCornerShape(14.dp), color = AmberBg, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        stringResource(R.string.discovery_unassigned_fmt, unassigned),
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberText,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            GoldButton(stringResource(R.string.discovery_continue), onClick = onContinue)
        }

        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun DiscoveredCard(asset: FinancialAsset) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val splits = asset.splits.mapNotNull { split ->
        DemoRepository.nomineeById(split.nomineeId)?.let { split to it }
    }
    val footer = if (splits.isEmpty()) {
        stringResource(R.string.asset_no_nominee)
    } else {
        splits.joinToString(" · ") { "${it.second.name} · ${it.first.percent}%" }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(320)) +
            slideInVertically(animationSpec = tween(320), initialOffsetY = { it / 2 })
    ) {
        MoneyCard(
            emoji = emojiFor(asset.kind),
            institution = asset.institution,
            maskedId = asset.maskedId,
            amount = formatRupees(asset.valueRupees),
            footer = footer,
            footerColor = if (splits.isEmpty()) RedAlert else Muted,
            chip = stringResource(R.string.discovery_via_fmt, asset.discoveredVia) to ChipKind.GREY
        )
    }
}
