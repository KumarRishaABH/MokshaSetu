package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DataMode
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.UserRole
import MoonshotApp.MokshaSetu.ui.DiyaMark
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.GoldSoft
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.NavyDeep
import MoonshotApp.MokshaSetu.ui.theme.OnNavySoft
import MoonshotApp.MokshaSetu.ui.theme.Paper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoleChooserScreen(onChoose: (UserRole) -> Unit) {
    val brand = stringResource(R.string.brand_title)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Navy, NavyDeep)))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(52.dp))
        DiyaMark(52.dp)
        Spacer(Modifier.height(16.dp))
        Text(
            buildAnnotatedString {
                append("Moksha")
                pushStyle(SpanStyle(color = GoldSoft, fontStyle = FontStyle.Italic, fontFamily = FontFamily.Serif))
                append("Setu")
                pop()
            },
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            modifier = Modifier.semantics { contentDescription = brand }
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.role_tagline),
            color = OnNavySoft,
            fontSize = 12.5.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))

        RoleCard(
            emoji = "🌱",
            title = stringResource(R.string.role_planner_title),
            body = stringResource(R.string.role_planner_body),
            footer = stringResource(R.string.role_planner_footer),
            onClick = { onChoose(UserRole.PLANNER) }
        )
        Spacer(Modifier.height(14.dp))
        RoleCard(
            emoji = "🕊️",
            title = stringResource(R.string.role_nominee_title),
            body = stringResource(R.string.role_nominee_body),
            footer = stringResource(R.string.role_nominee_footer),
            onClick = { onChoose(UserRole.NOMINEE) }
        )

        Spacer(Modifier.height(22.dp))
        SessionModeToggle()
        Spacer(Modifier.height(18.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TrustChip(stringResource(R.string.role_stack_aadhaar))
            TrustChip(stringResource(R.string.role_stack_digilocker))
            TrustChip(stringResource(R.string.role_stack_aa))
            TrustChip(stringResource(R.string.role_stack_udgam))
            TrustChip(stringResource(R.string.role_trust_chip), highlight = true)
        }
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun RoleCard(
    emoji: String,
    title: String,
    body: String,
    footer: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Paper,
        modifier = Modifier.fillMaxWidth().noRippleClickable(onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Navy,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(emoji, fontSize = 26.sp)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = Navy)
                Spacer(Modifier.height(4.dp))
                Text(body, fontSize = 11.5.sp, lineHeight = 16.sp, color = Muted)
                Spacer(Modifier.height(6.dp))
                Text(
                    footer,
                    fontSize = 9.sp,
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
            }
            Text("→", fontSize = 18.sp, color = Navy, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TrustChip(text: String, highlight: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (highlight) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.07f),
        border = BorderStroke(
            1.dp,
            if (highlight) GoldSoft.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.16f)
        )
    ) {
        Text(
            text,
            fontSize = 9.5.sp,
            letterSpacing = 0.4.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) GoldSoft else OnNavySoft,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SessionModeToggle() {
    val scratch = DemoRepository.dataMode == DataMode.SCRATCH
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            stringResource(R.string.role_mode_label),
            fontSize = 9.sp,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B93B3)
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModePill(stringResource(R.string.role_mode_scratch), scratch) {
                DemoRepository.chooseDataMode(DataMode.SCRATCH)
            }
            ModePill(stringResource(R.string.role_mode_demo), !scratch) {
                DemoRepository.chooseDataMode(DataMode.DEMO)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(if (scratch) R.string.role_mode_hint_scratch else R.string.role_mode_hint_demo),
            fontSize = 9.5.sp,
            lineHeight = 14.sp,
            textAlign = TextAlign.Center,
            color = OnNavySoft
        )
    }
}

@Composable
private fun ModePill(text: String, active: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (active) Gold else Color.White.copy(alpha = 0.07f),
        border = BorderStroke(1.dp, if (active) Gold else Color.White.copy(alpha = 0.16f)),
        modifier = Modifier.noRippleClickable(onClick)
    ) {
        Text(
            text,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) Navy else OnNavySoft,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
