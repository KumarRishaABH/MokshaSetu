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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.Fixtures
import MoonshotApp.MokshaSetu.data.UserRole
import MoonshotApp.MokshaSetu.data.services.MockAadhaarAuthService
import MoonshotApp.MokshaSetu.ui.DiyaMark
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.VirasatTextField
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.GoldSoft
import MoonshotApp.MokshaSetu.ui.theme.GreenBg
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.NavyDeep
import MoonshotApp.MokshaSetu.ui.theme.OnNavySoft
import MoonshotApp.MokshaSetu.ui.theme.Paper
import MoonshotApp.MokshaSetu.ui.theme.RedAlert
import kotlinx.coroutines.launch

@Composable
fun AadhaarLoginScreen(
    role: UserRole,
    onBack: () -> Unit,
    onAuthenticated: () -> Unit
) {
    val demoAadhaar = if (role == UserRole.PLANNER) Fixtures.PLANNER_AADHAAR else Fixtures.NOMINEE_AADHAAR
    val scope = rememberCoroutineScope()

    var aadhaar by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var txnId by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val otpSentMessage = stringResource(R.string.aadhaar_error_otp_send)
    val otpWrongMessage = stringResource(R.string.aadhaar_error_otp_wrong)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Navy, NavyDeep)))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.aadhaar_back),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.noRippleClickable(onBack).padding(vertical = 6.dp)
        )
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DiyaMark(34.dp)
            Column {
                Text(
                    stringResource(
                        if (role == UserRole.PLANNER) R.string.aadhaar_title_planner else R.string.aadhaar_title_nominee
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    stringResource(R.string.aadhaar_uidai_badge),
                    fontSize = 9.5.sp,
                    letterSpacing = 1.sp,
                    color = GoldSoft
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            stringResource(
                if (role == UserRole.PLANNER) R.string.aadhaar_body_planner else R.string.aadhaar_body_nominee
            ),
            color = OnNavySoft,
            fontSize = 11.5.sp,
            lineHeight = 18.sp
        )
        Spacer(Modifier.height(20.dp))

        Surface(shape = RoundedCornerShape(18.dp), color = Paper, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                VirasatTextField(
                    value = formatAadhaarInput(aadhaar),
                    onValueChange = { input ->
                        aadhaar = input.filter { it.isDigit() }.take(MockAadhaarAuthService.AADHAAR_LENGTH)
                    },
                    label = stringResource(R.string.aadhaar_field_label),
                    keyboardType = KeyboardType.NumberPassword,
                    enabled = txnId == null && !busy
                )

                if (txnId == null) {
                    GoldButton(
                        text = if (busy) {
                            stringResource(R.string.aadhaar_sending_otp)
                        } else {
                            stringResource(R.string.aadhaar_send_otp)
                        },
                        enabled = aadhaar.length == MockAadhaarAuthService.AADHAAR_LENGTH && !busy
                    ) {
                        error = null
                        busy = true
                        scope.launch {
                            val id = DemoRepository.aadhaarAuth.sendOtp(aadhaar)
                            busy = false
                            if (id == null) error = otpSentMessage else txnId = id
                        }
                    }
                } else {
                    Surface(shape = RoundedCornerShape(10.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(R.string.aadhaar_otp_sent_fmt, aadhaar.takeLast(4)),
                            color = GreenOk,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    VirasatTextField(
                        value = otp,
                        onValueChange = { input ->
                            otp = input.filter { it.isDigit() }.take(MockAadhaarAuthService.OTP_LENGTH)
                        },
                        label = stringResource(R.string.aadhaar_otp_label),
                        keyboardType = KeyboardType.NumberPassword,
                        enabled = !busy
                    )
                    GoldButton(
                        text = if (busy) {
                            stringResource(R.string.aadhaar_verifying)
                        } else {
                            stringResource(R.string.aadhaar_verify)
                        },
                        enabled = otp.length == MockAadhaarAuthService.OTP_LENGTH && !busy
                    ) {
                        error = null
                        busy = true
                        scope.launch {
                            val profile = DemoRepository.aadhaarAuth.verifyOtp(txnId.orEmpty(), otp)
                            busy = false
                            if (profile == null) {
                                error = otpWrongMessage
                            } else {
                                DemoRepository.signIn(role, profile)
                                onAuthenticated()
                            }
                        }
                    }
                }

                error?.let {
                    Text(it, color = RedAlert, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF3F5FA),
                    modifier = Modifier.fillMaxWidth().noRippleClickable {
                        aadhaar = demoAadhaar
                        otp = if (txnId == null) otp else Fixtures.DEMO_OTP
                    }
                ) {
                    Column(Modifier.padding(11.dp)) {
                        Text(
                            stringResource(R.string.aadhaar_demo_label),
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Muted
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            stringResource(
                                R.string.aadhaar_demo_value_fmt,
                                formatAadhaarInput(demoAadhaar),
                                Fixtures.DEMO_OTP
                            ),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy
                        )
                        Text(
                            stringResource(R.string.aadhaar_demo_hint),
                            fontSize = 9.5.sp,
                            color = Muted
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            stringResource(R.string.aadhaar_footer),
            color = Color(0xFF8B93B3),
            fontSize = 9.5.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(28.dp))
    }
}

private fun formatAadhaarInput(digits: String): String =
    digits.chunked(4).joinToString(" ")
