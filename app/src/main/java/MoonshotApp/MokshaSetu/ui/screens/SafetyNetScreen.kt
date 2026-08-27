package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.InfoCard
import MoonshotApp.MokshaSetu.ui.theme.AmberBg
import MoonshotApp.MokshaSetu.ui.theme.AmberText
import MoonshotApp.MokshaSetu.ui.theme.GreyBg

@Composable
fun SafetyNetScreen(onRestart: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { BannerCard(GreyBg, stringResource(R.string.safety_intro)) }
        item { InfoCard(null, stringResource(R.string.safety_c1_title), stringResource(R.string.safety_c1_body)) }
        item { InfoCard(null, stringResource(R.string.safety_c2_title), stringResource(R.string.safety_c2_body)) }
        item { InfoCard(null, stringResource(R.string.safety_c3_title), stringResource(R.string.safety_c3_body)) }
        item { InfoCard(null, stringResource(R.string.safety_c4_title), stringResource(R.string.safety_c4_body)) }
        item { InfoCard(null, stringResource(R.string.safety_c5_title), stringResource(R.string.safety_c5_body)) }
        item { BannerCard(AmberBg, stringResource(R.string.safety_trustee_note), AmberText) }
        item {
            Spacer(Modifier.height(4.dp))
            GoldButton(stringResource(R.string.safety_restart_cta), onClick = onRestart)
            Spacer(Modifier.height(14.dp))
        }
    }
}
