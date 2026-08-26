package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.InfoCard
import MoonshotApp.MokshaSetu.ui.ScoreRing
import MoonshotApp.MokshaSetu.ui.SectionTitle
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.Paper

@Composable
fun HomeScreen(
    onOpenVault: () -> Unit,
    onOpenNominees: () -> Unit,
    onOpenWishes: () -> Unit,
    onOpenSaarthi: () -> Unit,
    onOpenTriggers: () -> Unit,
    onPreviewNomineeJourney: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box(Modifier.fillMaxWidth().padding(top = 18.dp), contentAlignment = Alignment.Center) {
                ScoreRing(DemoRepository.legacyScore(), stringResource(R.string.home_score_label))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickTile("🗄️", stringResource(R.string.home_quick_vault), stringResource(R.string.home_quick_vault_sub, DemoRepository.assets.size), Modifier.weight(1f), onOpenVault)
                QuickTile("👥", stringResource(R.string.home_quick_nominees), stringResource(R.string.home_quick_nominees_sub, DemoRepository.nominees.size), Modifier.weight(1f), onOpenNominees)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickTile("💌", stringResource(R.string.home_quick_wishes), stringResource(R.string.home_quick_wishes_sub, DemoRepository.wishes.size), Modifier.weight(1f), onOpenWishes)
                QuickTile("🪔", stringResource(R.string.home_quick_saarthi), stringResource(R.string.home_quick_saarthi_sub), Modifier.weight(1f), onOpenSaarthi)
            }
        }
        item { SectionTitle(stringResource(R.string.home_attention_title)) }
        item {
            InfoCard(
                "📱",
                stringResource(R.string.attention_sim_title),
                stringResource(R.string.attention_sim_body),
                chip = stringResource(R.string.chip_add) to ChipKind.RED,
                onClick = onOpenVault
            )
        }
        item {
            InfoCard(
                "🏦",
                stringResource(R.string.attention_hdfc_title),
                stringResource(R.string.attention_hdfc_body),
                chip = stringResource(R.string.chip_ready) to ChipKind.GREEN
            )
        }
        item {
            InfoCard(
                "⚡",
                stringResource(R.string.attention_trigger_card_title),
                stringResource(R.string.attention_trigger_card_body),
                chip = stringResource(R.string.chip_review) to ChipKind.AMBER,
                onClick = onOpenTriggers
            )
        }
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Paper,
                border = BorderStroke(1.dp, LineC),
                modifier = Modifier.fillMaxWidth().noRippleClickable(onPreviewNomineeJourney)
            ) {
                Text(
                    stringResource(R.string.nominee_journey_hint),
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp,
                    color = Navy,
                    modifier = Modifier.padding(14.dp).fillMaxWidth()
                )
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun QuickTile(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Paper,
        border = BorderStroke(1.dp, LineC),
        modifier = modifier.noRippleClickable(onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.height(5.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text(subtitle, fontSize = 10.sp, color = Muted)
        }
    }
}
