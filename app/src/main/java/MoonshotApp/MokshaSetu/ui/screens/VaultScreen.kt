package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.AssetEntry
import MoonshotApp.MokshaSetu.data.Category
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.NoteKey
import MoonshotApp.MokshaSetu.data.Status
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.DashedActionCard
import MoonshotApp.MokshaSetu.ui.InfoCard
import MoonshotApp.MokshaSetu.ui.SectionTitle

@Composable
fun VaultScreen() {
    var showAdd by remember { mutableStateOf(false) }
    val assetsFinancial = DemoRepository.assets.filter { it.category == Category.FINANCIAL }
    val assetsDigital = DemoRepository.assets.filter { it.category == Category.DIGITAL }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { BannerCard(MaterialTheme.colorScheme.surfaceVariant, stringResource(R.string.vault_zk_banner)) }
        item { SectionTitle(stringResource(R.string.section_financial)) }
        items(assetsFinancial) { asset -> AssetCard(asset) }
        item { SectionTitle(stringResource(R.string.section_digital)) }
        items(assetsDigital) { asset -> AssetCard(asset) }
        item {
            DashedActionCard(
                stringResource(R.string.add_asset_title),
                stringResource(R.string.assign_nominee_sub),
                onClick = { showAdd = true }
            )
            Spacer(Modifier.height(12.dp))
        }
    }

    if (showAdd) {
        AddAssetDialog(onDismiss = { showAdd = false })
    }
}

@Composable
private fun AssetCard(asset: AssetEntry) {
    val chip: Pair<String, ChipKind> = when (asset.status) {
        Status.READY -> stringResource(R.string.chip_ready) to ChipKind.GREEN
        Status.TODO -> stringResource(R.string.chip_todo) to ChipKind.AMBER
        Status.ACTION -> stringResource(R.string.chip_set) to ChipKind.RED
    }
    val note = when (asset.noteRes) {
        NoteKey.SEALED -> if (asset.nominee != null) {
            stringResource(R.string.vault_nominee_fmt, asset.nominee, asset.nomineeRelation ?: "")
        } else {
            stringResource(R.string.vault_access_sealed)
        }
        NoteKey.PENDING -> stringResource(R.string.vault_nominee_pending)
        NoteKey.BLOCK_RECYCLE -> stringResource(R.string.vault_action_block_recycle)
        NoteKey.MEMORIALISE -> stringResource(R.string.vault_wish_memorialise)
        NoteKey.AUTO_CANCEL -> stringResource(R.string.vault_auto_cancel)
    }
    InfoCard(asset.emoji, asset.title, note, chip = chip)
}

@Composable
private fun AddAssetDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_asset_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.add_asset_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    label = { Text(stringResource(R.string.add_asset_secret_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) DemoRepository.addAsset("🗂️", name.trim(), secret)
                onDismiss()
            }) { Text(stringResource(R.string.add_asset_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.add_asset_cancel)) }
        }
    )
}
