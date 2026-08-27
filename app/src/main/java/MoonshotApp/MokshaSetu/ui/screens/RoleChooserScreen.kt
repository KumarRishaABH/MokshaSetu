package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import MoonshotApp.MokshaSetu.data.UserRole
import MoonshotApp.MokshaSetu.ui.DiyaMark
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.GoldSoft
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.NavyDeep
import MoonshotApp.MokshaSetu.ui.theme.OnNavySoft
import MoonshotApp.MokshaSetu.ui.theme.Paper

@Composable
fun RoleChooserScreen(onChoose: (UserRole) -> Unit) {
    val brand = stringResource(R.string.brand_title)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Navy, NavyDeep)))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(52.dp))
        DiyaMark(52.dp)
        Spacer(Modifier.height(16.dp))
        Text(
            buildAnnotatedString {
                append("Vi")
                pushStyle(SpanStyle(color = GoldSoft, fontStyle = FontStyle.Italic, fontFamily = FontFamily.Serif))
                append("रा")
                pop()
                append("sat")
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
            lineHeight = 19.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(30.dp))

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

        Spacer(Modifier.height(26.dp))
        Text(
            stringResource(R.string.role_trust_note),
            color = Color(0xFF8B93B3),
            fontSize = 10.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center
        )
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
        Column(Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(emoji, fontSize = 26.sp)
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Navy,
                    modifier = Modifier.weight(1f)
                )
                Text("→", fontSize = 18.sp, color = Navy, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text(body, fontSize = 11.5.sp, lineHeight = 17.sp, color = Muted)
            Spacer(Modifier.height(10.dp))
            Text(footer, fontSize = 9.5.sp, letterSpacing = 0.8.sp, fontWeight = FontWeight.Bold, color = Navy)
        }
    }
}
