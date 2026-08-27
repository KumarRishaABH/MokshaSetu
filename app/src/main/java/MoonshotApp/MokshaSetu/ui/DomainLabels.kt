package MoonshotApp.MokshaSetu.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.AssetKind
import MoonshotApp.MokshaSetu.data.DigitalAction

fun emojiFor(kind: AssetKind): String = when (kind) {
    AssetKind.BANK -> "🏦"
    AssetKind.DEMAT -> "📈"
    AssetKind.INSURANCE -> "🛡️"
    AssetKind.PROPERTY -> "🏠"
}

@Composable
fun sectionLabelFor(kind: AssetKind): String = stringResource(
    when (kind) {
        AssetKind.BANK -> R.string.kind_bank
        AssetKind.DEMAT -> R.string.kind_demat
        AssetKind.INSURANCE -> R.string.kind_insurance
        AssetKind.PROPERTY -> R.string.kind_property
    }
)

@Composable
fun labelFor(action: DigitalAction): String = stringResource(
    when (action) {
        DigitalAction.MEMORIALISE -> R.string.action_memorialise
        DigitalAction.DELETE -> R.string.action_delete
        DigitalAction.TRANSFER_ACCESS -> R.string.action_transfer
    }
)
