package MoonshotApp.MokshaSetu.ui.screens.nominee

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.RegistryState
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.CheckRow
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.OutlineButton
import MoonshotApp.MokshaSetu.ui.theme.GreenBg
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Paper
import MoonshotApp.MokshaSetu.ui.theme.RedAlert
import MoonshotApp.MokshaSetu.ui.theme.RedBg
import kotlinx.coroutines.delay

@Composable
fun RegistryVerifyScreen(onVerified: () -> Unit, onRetry: () -> Unit) {
    val cert = DemoRepository.deathCert
    val state = DemoRepository.registryState
    var step by remember { mutableIntStateOf(0) }

    LaunchedEffect(cert) {
        if (cert == null) return@LaunchedEffect
        DemoRepository.updateRegistryState(RegistryState.CHECKING)
        step = 0
        repeat(CHECK_STEPS) {
            delay(520)
            step = it + 1
        }
        DemoRepository.updateRegistryState(DemoRepository.deathRegistry.verify(cert))
    }

    val pulse = rememberInfiniteTransition(label = "registry-pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "registry-pulse-alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(10.dp))

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                if (state == RegistryState.VERIFIED) "🛡️" else if (state == RegistryState.MISMATCH) "⚠️" else "🔎",
                fontSize = 40.sp,
                modifier = if (state == RegistryState.CHECKING) Modifier.alpha(pulseAlpha) else Modifier
            )
        }
        Text(
            stringResource(R.string.registry_hero),
            style = MaterialTheme.typography.titleLarge,
            color = Navy,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            stringResource(R.string.registry_hero_sub),
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = Muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Surface(shape = RoundedCornerShape(14.dp), color = Paper, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                CheckRow(
                    text = stringResource(R.string.registry_step_digilocker),
                    done = step >= 1,
                    active = step == 0
                )
                CheckRow(
                    text = stringResource(R.string.registry_step_crs),
                    done = state == RegistryState.VERIFIED,
                    active = step >= 1 && state == RegistryState.CHECKING
                )
                CheckRow(
                    text = stringResource(R.string.registry_step_aadhaar),
                    done = state == RegistryState.VERIFIED,
                    active = step >= 2 && state == RegistryState.CHECKING
                )
            }
        }

        AnimatedVisibility(visible = state == RegistryState.VERIFIED, enter = fadeIn()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(14.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(
                            stringResource(R.string.registry_verified_title),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenOk
                        )
                        cert?.let {
                            RecordRow(stringResource(R.string.registry_record_name), it.deceasedName)
                            RecordRow(stringResource(R.string.registry_record_reg), it.registrationNo)
                            RecordRow(stringResource(R.string.registry_record_state), it.state)
                            RecordRow(stringResource(R.string.registry_record_issued), it.issuedOn)
                        }
                    }
                }
                BannerCard(GreyBg, stringResource(R.string.registry_verified_note))
                GoldButton(stringResource(R.string.registry_continue), onClick = onVerified)
            }
        }

        AnimatedVisibility(visible = state == RegistryState.MISMATCH, enter = fadeIn()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(14.dp), color = RedBg, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            stringResource(R.string.registry_mismatch_title),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedAlert
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            stringResource(R.string.registry_mismatch_body),
                            fontSize = 10.5.sp,
                            lineHeight = 16.sp,
                            color = Navy
                        )
                    }
                }
                OutlineButton(stringResource(R.string.registry_retry), onClick = onRetry)
            }
        }

        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun RecordRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontSize = 10.sp, color = Muted, modifier = Modifier.weight(0.42f))
        Text(
            value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Navy,
            modifier = Modifier.weight(0.58f)
        )
    }
}

private const val CHECK_STEPS = 3
