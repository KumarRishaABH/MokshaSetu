package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.ui.DiyaMark
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.GoldSoft
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Navy2

@Composable
fun LoginScreen(onEnterPlanner: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Navy, Color(0xFF0B1430))))
            .padding(horizontal = 30.dp)
    ) {
        DiyaMark(56.dp)
        Spacer(Modifier.height(18.dp))
        Text(
            buildAnnotatedString {
                append("Vi")
                pushStyle(SpanStyle(color = GoldSoft, fontStyle = FontStyle.Italic, fontFamily = FontFamily.Serif))
                append("रा")
                pop()
                append("sat")
            },
            style = MaterialTheme.typography.displayLarge,
            color = Color.White
        )
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.login_tagline),
            color = Color(0xFFB9C1DB),
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(Modifier.height(26.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().noRippleClickable(onEnterPlanner)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 13.dp)
            ) {
                Text("🔐 ${stringResource(R.string.login_aadhaar)} ", color = Navy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF138808)) {
                    Text(
                        stringResource(R.string.login_aadhaar_badge),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x59FFFFFF)),
            modifier = Modifier.fillMaxWidth().noRippleClickable(onEnterPlanner)
        ) {
            Text(
                "📄 ${stringResource(R.string.login_digilocker)}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(vertical = 13.dp)
            )
        }
        Spacer(Modifier.height(22.dp))
        Text(
            stringResource(R.string.login_trust),
            color = Color(0xFF8B93B3),
            fontSize = 10.sp,
            lineHeight = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(30.dp))
    }
}
