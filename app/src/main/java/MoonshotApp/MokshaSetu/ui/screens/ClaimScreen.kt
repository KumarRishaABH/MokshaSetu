package MoonshotApp.MokshaSetu.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.ClaimDotState
import MoonshotApp.MokshaSetu.data.ClaimStep
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.StatusChip
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.LineC

@Composable
fun ClaimScreen(onOpenSafetyNet: () -> Unit) {
    val steps = listOf(
        ClaimStep(ClaimDotState.DONE, null, R.string.claim_step1_title, R.string.claim_step1_chip, R.string.claim_step1_detail_title, R.string.claim_step1_detail_body),
        ClaimStep(ClaimDotState.DONE, null, R.string.claim_step2_title, null, R.string.claim_step2_detail_title, R.string.claim_step2_detail_body),
        ClaimStep(ClaimDotState.DONE, null, R.string.claim_step3_title, null, R.string.claim_step3_detail_title, R.string.claim_step3_detail_body),
        ClaimStep(ClaimDotState.NOW, 4, R.string.claim_step4_title, null, R.string.claim_step4_detail_title, R.string.claim_step4_detail_body),
        ClaimStep(ClaimDotState.TODO, 5, R.string.claim_step5_title, null, R.string.claim_step5_detail_title, R.string.claim_step5_detail_body)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF20305C), Color(0xFF0E1A3C))))
                    .padding(vertical = 18.dp)
            ) {
                Text(
                    stringResource(R.string.claim_head_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.claim_head_subtitle),
                    fontSize = 10.5.sp,
                    color = Color(0xFFB9C1DB)
                )
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
        items(steps.size) { index -> ClaimStepRow(steps[index], isLast = index == steps.lastIndex) }
        item {
            Spacer(Modifier.height(14.dp))
            GoldButton(stringResource(R.string.claim_safetynet_cta), onClick = onOpenSafetyNet)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ClaimStepRow(step: ClaimStep, isLast: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val dotColor = when (step.dotState) {
                ClaimDotState.DONE -> GreenOk
                ClaimDotState.NOW -> Gold
                ClaimDotState.TODO -> Color(0xFFCFD4E0)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(24.dp).clip(CircleShape).background(dotColor)
            ) {
                when (step.dotState) {
                    ClaimDotState.DONE -> Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    ClaimDotState.NOW -> Text("${step.stepNo}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    ClaimDotState.TODO -> Text("${step.stepNo}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .height(64.dp)
                        .background(LineC)
                )
            }
        }
        Column(Modifier.padding(bottom = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(step.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                step.chipRes?.let { StatusChip(stringResource(it), ChipKind.GREEN) }
            }
            Text(
                stringResource(step.detailTitleRes),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                stringResource(step.detailBodyRes),
                fontSize = 10.5.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
