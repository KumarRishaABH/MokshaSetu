package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DataMode
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.Fixtures
import MoonshotApp.MokshaSetu.data.UserRole
import MoonshotApp.MokshaSetu.data.remote.RegistryUnavailableException
import MoonshotApp.MokshaSetu.data.services.MockAadhaarAuthService
import MoonshotApp.MokshaSetu.ui.DiyaMark
import android.util.Log
import kotlinx.coroutines.CancellationException
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.GoldSoft
import MoonshotApp.MokshaSetu.ui.theme.GreenBg
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.Ink
import MoonshotApp.MokshaSetu.ui.theme.LineC
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
    val scope = rememberCoroutineScope()

    var aadhaar by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var txnId by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var registryOnline by remember { mutableStateOf<Boolean?>(null) }

    val notRegisteredMessage = stringResource(R.string.aadhaar_error_not_registered)
    val serverUnreachableMessage = stringResource(R.string.aadhaar_error_server_unreachable)
    val otpWrongMessage = stringResource(R.string.aadhaar_error_otp_wrong)

    LaunchedEffect(Unit) {
        registryOnline = DemoRepository.aadhaarRegistry.isReachable()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Navy, NavyDeep)))
            .statusBarsPadding()
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

        RegistryStatusRow(registryOnline)

        Spacer(Modifier.height(10.dp))

        Surface(shape = RoundedCornerShape(18.dp), color = Paper, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SegmentedNumberInput(
                    value = aadhaar,
                    onValueChange = { aadhaar = it },
                    groupSize = 4,
                    maxLength = MockAadhaarAuthService.AADHAAR_LENGTH,
                    label = stringResource(R.string.aadhaar_field_label),
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
                            val id = try {
                                DemoRepository.aadhaarAuth.sendOtp(aadhaar)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                Log.e("AadhaarLogin", "sendOtp registry call failed", e)
                                busy = false
                                error = serverUnreachableMessage
                                return@launch
                            }
                            busy = false
                            if (id == null) error = notRegisteredMessage else txnId = id
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
                    SegmentedNumberInput(
                        value = otp,
                        onValueChange = { otp = it },
                        groupSize = 1,
                        maxLength = MockAadhaarAuthService.OTP_LENGTH,
                        label = stringResource(R.string.aadhaar_otp_label),
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
                            val profile = try {
                                DemoRepository.aadhaarAuth.verifyOtp(txnId.orEmpty(), otp)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                Log.e("AadhaarLogin", "verifyOtp registry call failed", e)
                                busy = false
                                error = serverUnreachableMessage
                                return@launch
                            }
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

                DemoCredentialsTile(role) { digits ->
                    aadhaar = digits
                    otp = if (txnId == null) otp else Fixtures.DEMO_OTP
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

@Composable
private fun RegistryStatusRow(online: Boolean?) {
    val (labelRes, tint) = when (online) {
        null -> R.string.aadhaar_status_checking to Muted
        true -> R.string.aadhaar_status_connected to GreenOk
        false -> R.string.aadhaar_status_offline to RedAlert
    }
    Text(
        stringResource(labelRes),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = tint
    )
}

@Composable
private fun SegmentedNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    groupSize: Int,
    maxLength: Int,
    label: String,
    enabled: Boolean = true
) {
    val boxCount = maxLength / groupSize
    val focusRequesters = remember(boxCount) { List(boxCount) { FocusRequester() } }
    val chunks = value.chunked(groupSize)
    val cells = List(boxCount) { chunks.getOrElse(it) { "" } }

    Column {
        Text(
            label,
            fontSize = 9.5.sp,
            letterSpacing = 0.8.sp,
            fontWeight = FontWeight.Bold,
            color = Muted
        )
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(boxCount) { index ->
                var cellFocused by remember { mutableStateOf(false) }
                val filled = cells[index].length == groupSize
                BasicTextField(
                    value = cells[index],
                    onValueChange = { input ->
                        if (!enabled) return@BasicTextField
                        val digits = input.filter { it.isDigit() }.take(groupSize * (boxCount - index))
                        if (digits.isEmpty() && input.isNotEmpty()) return@BasicTextField
                        val newCells = cells.toMutableList()
                        newCells[index] = digits.take(groupSize)
                        var cell = index + 1
                        var remaining = digits.drop(groupSize)
                        while (remaining.isNotEmpty() && cell < boxCount) {
                            newCells[cell] = remaining.take(groupSize)
                            remaining = remaining.drop(groupSize)
                            cell++
                        }
                        onValueChange(newCells.joinToString(""))
                        val focusTarget = (index until boxCount)
                            .firstOrNull { newCells[it].length < groupSize }
                            ?: (boxCount - 1)
                        focusRequesters[focusTarget].requestFocus()
                    },
                    enabled = enabled,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = if (groupSize == 1) 18.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        color = Ink
                    ),
                    cursorBrush = SolidColor(Gold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .onPreviewKeyEvent { event ->
                            val hop = enabled &&
                                event.type == KeyEventType.KeyDown &&
                                event.key == Key.Backspace &&
                                index > 0 &&
                                cells[index].isEmpty()
                            if (hop) {
                                val newCells = cells.toMutableList()
                                newCells[index - 1] = cells[index - 1].dropLast(1)
                                onValueChange(newCells.joinToString(""))
                                focusRequesters[index - 1].requestFocus()
                            }
                            hop
                        }
                        .focusRequester(focusRequesters[index])
                        .onFocusChanged { cellFocused = it.isFocused }
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (cellFocused) Paper else Color(0xFFF3F5FA))
                        .border(
                            width = 1.dp,
                            color = when {
                                cellFocused -> Gold
                                filled -> GreenOk.copy(alpha = 0.45f)
                                else -> LineC
                            },
                            shape = RoundedCornerShape(12.dp)
                        ),
                    decorationBox = { innerField ->
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            innerField()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DemoCredentialsTile(role: UserRole, onFill: (String) -> Unit) {
    val vaultNominees = DemoRepository.nominees.mapNotNull { nominee ->
        nominee.demoAadhaar?.let { nominee.name to it }
    }
    val listVaultNominees = role == UserRole.NOMINEE && DemoRepository.dataMode == DataMode.SCRATCH

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF3F5FA),
        modifier = Modifier.fillMaxWidth()
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
            if (listVaultNominees && vaultNominees.isEmpty()) {
                Text(
                    stringResource(R.string.aadhaar_demo_nominee_empty),
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    color = Muted
                )
            } else if (listVaultNominees) {
                vaultNominees.forEach { (name, digits) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable { onFill(digits) }
                            .padding(vertical = 4.dp)
                    ) {
                        Text("→", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Gold)
                        Column {
                            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Navy)
                            Text(
                                stringResource(
                                    R.string.aadhaar_demo_value_fmt,
                                    formatAadhaarInput(digits),
                                    Fixtures.DEMO_OTP
                                ),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Navy
                            )
                        }
                    }
                }
            } else {
                val demoAadhaar = if (role == UserRole.PLANNER) Fixtures.PLANNER_AADHAAR else Fixtures.NOMINEE_AADHAAR
                Column(Modifier.fillMaxWidth().noRippleClickable { onFill(demoAadhaar) }) {
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
                }
            }
            Text(
                stringResource(R.string.aadhaar_demo_hint),
                fontSize = 9.5.sp,
                color = Muted
            )
        }
    }
}
