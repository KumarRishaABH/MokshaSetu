package MoonshotApp.MokshaSetu.ui.screens.nominee

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.ClaimState
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.FinancialAsset
import MoonshotApp.MokshaSetu.data.Nominee
import MoonshotApp.MokshaSetu.data.formatRupees
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.OutlineButton
import MoonshotApp.MokshaSetu.ui.SectionTitle
import MoonshotApp.MokshaSetu.ui.emojiFor
import MoonshotApp.MokshaSetu.ui.theme.Cream
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.GreenBg
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Paper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimScreen(onOpenSafetyNet: () -> Unit) {
    val nominee = DemoRepository.activeNominee()
    val assets = nominee?.let { DemoRepository.entitlementsFor(it.id).assets }.orEmpty()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var confirming by remember { mutableStateOf<List<FinancialAsset>>(emptyList()) }

    val claims = assets.map { DemoRepository.claimFor(it.id) }
    val notStarted = assets.filter { DemoRepository.claimFor(it.id).state == ClaimState.NOT_STARTED }
    val credited = assets.filter { DemoRepository.claimFor(it.id).state == ClaimState.CREDITED }
    val allCredited = assets.isNotEmpty() && credited.size == assets.size
    val creditedTotal = nominee?.let { n -> credited.sumOf { it.shareRupeesFor(n.id) } } ?: 0L
    val inFlight = claims.any {
        it.state == ClaimState.PACKET_SENT || it.state == ClaimState.INSTITUTION_PROCESSING
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { BannerCard(GreyBg, stringResource(R.string.claim_guardrail_banner)) }

        if (notStarted.isNotEmpty()) {
            item {
                GoldButton(
                    stringResource(R.string.claim_all_cta_fmt, notStarted.size),
                    enabled = !inFlight
                ) { confirming = notStarted }
            }
        }

        item { SectionTitle(stringResource(R.string.claim_section_assets)) }

        items(assets.size, key = { "claim-${assets[it].id}" }) { index ->
            val asset = assets[index]
            ClaimCard(
                asset = asset,
                shareRupees = nominee?.let { asset.shareRupeesFor(it.id) } ?: 0L,
                enabled = !inFlight,
                onClaim = { confirming = listOf(asset) }
            )
        }

        if (allCredited) {
            item {
                Surface(shape = RoundedCornerShape(16.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.claim_done_title),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenOk
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatRupees(creditedTotal),
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = Navy
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            pluralStringResource(R.plurals.claim_done_body, credited.size, credited.size),
                            fontSize = 10.5.sp,
                            lineHeight = 16.sp,
                            color = Navy
                        )
                    }
                }
            }
            item {
                GoldButton(stringResource(R.string.claim_safetynet_cta), onClick = onOpenSafetyNet)
                Spacer(Modifier.height(16.dp))
            }
        } else {
            item {
                OutlineButton(stringResource(R.string.claim_safetynet_cta), onClick = onOpenSafetyNet)
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (confirming.isNotEmpty() && nominee != null) {
        val batch = confirming
        ModalBottomSheet(
            onDismissRequest = { confirming = emptyList() },
            sheetState = sheetState,
            containerColor = Paper
        ) {
            ConfirmSheet(
                assets = batch,
                nomineeId = nominee.id,
                onCancel = { confirming = emptyList() },
                onConfirm = {
                    confirming = emptyList()
                    scope.launch { runClaims(batch, nominee) }
                }
            )
        }
    }
}

private suspend fun runClaims(assets: List<FinancialAsset>, nominee: Nominee) {
    assets.forEach { asset ->
        val reference = DemoRepository.settlement.submitClaim(asset, nominee) { state ->
            DemoRepository.updateClaim(asset.id, state)
        }
        DemoRepository.updateClaim(asset.id, ClaimState.CREDITED, reference)
    }
}

@Composable
private fun ConfirmSheet(
    assets: List<FinancialAsset>,
    nomineeId: Int,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    val institutions = assets.map { it.institution }.distinct().joinToString(", ")
    val total = assets.sumOf { it.shareRupeesFor(nomineeId) }

    Column(
        Modifier.padding(horizontal = 20.dp).padding(bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            stringResource(R.string.claim_sheet_title),
            style = MaterialTheme.typography.titleLarge,
            color = Navy
        )
        Text(
            stringResource(R.string.claim_sheet_body_fmt, institutions, institutions),
            fontSize = 12.sp,
            lineHeight = 19.sp,
            color = Navy
        )
        Surface(shape = RoundedCornerShape(12.dp), color = Cream, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                assets.forEach { asset ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "${emojiFor(asset.kind)}  ${asset.institution}",
                            fontSize = 11.sp,
                            color = Navy,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            formatRupees(asset.shareRupeesFor(nomineeId)),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy
                        )
                    }
                }
                if (assets.size > 1) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(R.string.claim_sheet_total),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            formatRupees(total),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy
                        )
                    }
                }
            }
        }
        Text(
            stringResource(R.string.claim_sheet_note),
            fontSize = 10.sp,
            lineHeight = 15.sp,
            color = Muted
        )
        GoldButton(stringResource(R.string.claim_sheet_confirm), onClick = onConfirm)
        OutlineButton(stringResource(R.string.dialog_cancel), onClick = onCancel)
    }
}

@Composable
private fun ClaimCard(asset: FinancialAsset, shareRupees: Long, enabled: Boolean, onClaim: () -> Unit) {
    val claim = DemoRepository.claimFor(asset.id)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Paper,
        border = BorderStroke(1.dp, if (claim.state == ClaimState.CREDITED) GreenOk else LineC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Box(
                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(Cream),
                    contentAlignment = Alignment.Center
                ) { Text(emojiFor(asset.kind), fontSize = 18.sp) }
                Column(Modifier.weight(1f)) {
                    Text(asset.institution, style = MaterialTheme.typography.titleMedium, color = Navy)
                    Text(asset.maskedId, style = MaterialTheme.typography.bodySmall, color = Muted)
                }
                Text(
                    formatRupees(shareRupees),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Navy
                )
            }

            when (claim.state) {
                ClaimState.NOT_STARTED -> OutlineButton(
                    text = stringResource(R.string.claim_asset_cta_fmt, asset.institution),
                    enabled = enabled,
                    onClick = onClaim
                )
                ClaimState.PACKET_SENT -> ProgressRow(
                    stringResource(R.string.claim_state_packet_fmt, asset.institution)
                )
                ClaimState.INSTITUTION_PROCESSING -> ProgressRow(
                    stringResource(R.string.claim_state_processing_fmt, asset.institution)
                )
                ClaimState.CREDITED -> CreditedPanel(asset, shareRupees, claim.referenceNo)
            }
        }
    }
}

@Composable
private fun ProgressRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        CircularProgressIndicator(modifier = Modifier.size(15.dp), color = Gold, strokeWidth = 2.dp)
        Text(text, fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, color = Navy)
    }
}

@Composable
private fun CreditedPanel(asset: FinancialAsset, shareRupees: Long, referenceNo: String?) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { it / 3 }
    ) {
        Surface(shape = RoundedCornerShape(12.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(
                        R.string.claim_state_credited_fmt,
                        asset.institution,
                        formatRupees(shareRupees)
                    ),
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenOk
                )
                if (referenceNo != null) {
                    Text(
                        stringResource(R.string.claim_reference_fmt, referenceNo),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Navy
                    )
                }
                Text(
                    stringResource(R.string.claim_credited_note),
                    fontSize = 9.5.sp,
                    lineHeight = 14.sp,
                    color = Muted
                )
            }
        }
    }
}
