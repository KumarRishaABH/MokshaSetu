package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.Nominee
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.ChipKind
import MoonshotApp.MokshaSetu.ui.DashedActionCard
import MoonshotApp.MokshaSetu.ui.StatusChip
import MoonshotApp.MokshaSetu.ui.theme.AmberBg
import MoonshotApp.MokshaSetu.ui.theme.AmberText
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.LineC

@Composable
fun NomineesScreen() {
    var showAdd by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        items(DemoRepository.nominees) { nominee -> NomineeCard(nominee) }
        item {
            DashedActionCard(
                stringResource(R.string.assign_nominee_cta),
                stringResource(R.string.assign_nominee_sub),
                onClick = { showAdd = true }
            )
        }
        item { BannerCard(AmberBg, stringResource(R.string.dpdp_banner), AmberText) }
        item { Spacer(Modifier.height(12.dp)) }
    }

    if (showAdd) {
        AddNomineeDialog(onDismiss = { showAdd = false })
    }
}

@Composable
private fun NomineeCard(nominee: Nominee) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, LineC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(34.dp).clip(CircleShape).background(Gold)
            ) {
                Text(nominee.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f)) {
                Text(nominee.relation, style = MaterialTheme.typography.titleMedium)
                Text(nominee.scope, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            val chipText = if (nominee.verified) {
                stringResource(R.string.chip_verified)
            } else {
                stringResource(R.string.chip_invited)
            }
            StatusChip(chipText, if (nominee.verified) ChipKind.GREEN else ChipKind.AMBER)
        }
    }
}

@Composable
private fun AddNomineeDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_nominee_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.add_nominee_name_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = relation, onValueChange = { relation = it }, label = { Text(stringResource(R.string.add_nominee_relation_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = scope, onValueChange = { scope = it }, label = { Text(stringResource(R.string.add_nominee_scope_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank() && relation.isNotBlank()) {
                    DemoRepository.addNominee(name.trim(), relation.trim(), scope.trim().ifBlank { relation.trim() })
                }
                onDismiss()
            }) { Text(stringResource(R.string.add_nominee_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.add_asset_cancel)) }
        }
    )
}
