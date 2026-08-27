package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.Cream
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy

@Composable
fun NomineePickerDialog(
    title: String,
    selected: Int?,
    onDismiss: () -> Unit,
    onPick: (Int?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.picker_body),
                    fontSize = 10.5.sp,
                    lineHeight = 15.sp,
                    color = Muted
                )
                DemoRepository.nominees.forEach { nominee ->
                    PickerRow(
                        initial = nominee.initial,
                        title = nominee.name,
                        subtitle = stringResource(
                            R.string.picker_nominee_sub_fmt,
                            nominee.relation,
                            nominee.maskedAadhaar
                        ),
                        active = selected == nominee.id,
                        onClick = { onPick(nominee.id) }
                    )
                }
                PickerRow(
                    initial = "–",
                    title = stringResource(R.string.picker_none_title),
                    subtitle = stringResource(R.string.picker_none_sub),
                    active = selected == null,
                    onClick = { onPick(null) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_close)) }
        }
    )
}

@Composable
private fun PickerRow(
    initial: String,
    title: String,
    subtitle: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (active) Cream else Color.Transparent,
        border = BorderStroke(1.dp, if (active) Gold else LineC),
        modifier = Modifier.fillMaxWidth().noRippleClickable(onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp).fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(28.dp).clip(CircleShape).background(if (active) Gold else GreyBg)
            ) {
                Text(
                    initial,
                    color = if (active) Color.White else Muted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Navy)
                Text(subtitle, fontSize = 10.sp, color = Muted)
            }
            if (active) Text("✓", color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
