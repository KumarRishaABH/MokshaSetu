package MoonshotApp.MokshaSetu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.MetaKey
import MoonshotApp.MokshaSetu.data.Wish
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.DashedActionCard
import MoonshotApp.MokshaSetu.ui.EmptyStateCard
import MoonshotApp.MokshaSetu.ui.VirasatTextField
import MoonshotApp.MokshaSetu.ui.theme.GreyBg

@Composable
fun WishesScreen() {
    var showAdd by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(6.dp)) }
        item { BannerCard(GreyBg, stringResource(R.string.wishes_banner)) }
        if (DemoRepository.wishes.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.wishes_empty_title),
                    body = stringResource(R.string.wishes_empty_body)
                )
            }
        }
        items(DemoRepository.wishes, key = { it.id }) { wish -> WishCard(wish) }
        item {
            DashedActionCard(
                stringResource(R.string.wishes_add_cta),
                stringResource(R.string.wishes_add_sub),
                onClick = { showAdd = true }
            )
            Spacer(Modifier.height(12.dp))
        }
    }

    if (showAdd) {
        AddWishDialog(onDismiss = { showAdd = false })
    }
}

@Composable
private fun WishCard(wish: Wish) {
    Surface(
        shape = RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp, bottomStart = 14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(3.dp).fillMaxHeight().background(MaterialTheme.colorScheme.secondary))
            Column(modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 14.dp, bottom = 12.dp)) {
                Text(
                    text = wish.customText ?: stringResource(wish.textRes ?: R.string.app_name),
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(6.dp))
                Text(metaText(wish), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun metaText(wish: Wish): String = when (wish.metaRes) {
    MetaKey.ROHAN -> if (wish.customMeta != null) {
        stringResource(R.string.wish_custom_meta_fmt, wish.customMeta)
    } else {
        stringResource(R.string.wish_fd_meta, nomineeName(1))
    }
    MetaKey.TEMPLE -> stringResource(R.string.wish_temple_meta)
    MetaKey.INSTA -> stringResource(R.string.wish_insta_meta, nomineeName(2))
    MetaKey.VIDEO -> stringResource(R.string.wish_video_meta, nomineeName(3))
}

@Composable
private fun nomineeName(nomineeId: Int): String =
    DemoRepository.nomineeById(nomineeId)?.name?.substringBefore(' ')
        ?: stringResource(R.string.wishes_default_recipient)

@Composable
private fun AddWishDialog(onDismiss: () -> Unit) {
    var message by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("") }
    val defaultRecipient = stringResource(R.string.wishes_default_recipient)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.wishes_add_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                VirasatTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = stringResource(R.string.wishes_message_label),
                    singleLine = false
                )
                VirasatTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = stringResource(R.string.wishes_recipient_label)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (message.isNotBlank()) {
                    DemoRepository.addWish(
                        message.trim(),
                        recipient.trim().ifBlank { defaultRecipient }
                    )
                }
                onDismiss()
            }) { Text(stringResource(R.string.wishes_add_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_cancel)) }
        }
    )
}
