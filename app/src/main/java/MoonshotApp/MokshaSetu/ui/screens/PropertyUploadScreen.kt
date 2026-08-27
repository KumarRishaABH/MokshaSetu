package MoonshotApp.MokshaSetu.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.formatRupees
import MoonshotApp.MokshaSetu.ui.BannerCard
import MoonshotApp.MokshaSetu.ui.GoldButton
import MoonshotApp.MokshaSetu.ui.OutlineButton
import MoonshotApp.MokshaSetu.ui.SectionTitle
import MoonshotApp.MokshaSetu.ui.VirasatTextField
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.GreenBg
import MoonshotApp.MokshaSetu.ui.theme.GreenOk
import MoonshotApp.MokshaSetu.ui.theme.GreyBg
import MoonshotApp.MokshaSetu.ui.theme.Muted
import MoonshotApp.MokshaSetu.ui.theme.Navy
import MoonshotApp.MokshaSetu.ui.theme.RedAlert

@Composable
fun PropertyUploadScreen(onSaved: () -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf<String?>(null) }
    var nomineeId by remember { mutableStateOf<Int?>(null) }
    var picking by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) fileName = displayNameOf(context, uri)
    }

    val nominee = DemoRepository.nomineeById(nomineeId)
    val valueRupees = value.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val canSave = title.isNotBlank() && fileName != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(6.dp))
        BannerCard(GreyBg, stringResource(R.string.property_banner))

        SectionTitle(stringResource(R.string.property_section_details))
        VirasatTextField(
            value = title,
            onValueChange = { title = it },
            label = stringResource(R.string.property_title_label)
        )
        VirasatTextField(
            value = value,
            onValueChange = { value = it.filter { char -> char.isDigit() }.take(12) },
            label = stringResource(R.string.property_value_label),
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
            supportingText = if (valueRupees > 0) formatRupees(valueRupees) else null
        )

        SectionTitle(stringResource(R.string.property_section_doc))
        OutlineButton(
            text = if (fileName == null) {
                stringResource(R.string.property_pick_doc)
            } else {
                stringResource(R.string.property_change_doc)
            }
        ) { picker.launch("*/*") }

        fileName?.let { name ->
            Surface(shape = RoundedCornerShape(12.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text("📄", fontSize = 14.sp)
                    Column {
                        Text(
                            name,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy
                        )
                        Text(
                            stringResource(R.string.property_doc_stored),
                            fontSize = 9.5.sp,
                            color = GreenOk
                        )
                    }
                }
            }
        }

        SectionTitle(stringResource(R.string.property_section_nominee))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GreyBg,
            modifier = Modifier.fillMaxWidth().noRippleClickable { picking = true }
        ) {
            Text(
                if (nominee == null) {
                    stringResource(R.string.property_pick_nominee)
                } else {
                    stringResource(R.string.property_nominee_fmt, nominee.name, nominee.relation)
                },
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (nominee == null) RedAlert else Navy,
                modifier = Modifier.padding(13.dp)
            )
        }

        Spacer(Modifier.height(2.dp))
        GoldButton(stringResource(R.string.property_save), enabled = canSave) {
            DemoRepository.addProperty(
                title = title.trim(),
                fileName = fileName.orEmpty(),
                valueRupees = valueRupees,
                nomineeId = nomineeId
            )
            onSaved()
        }
        Text(
            stringResource(R.string.property_footer),
            fontSize = 9.5.sp,
            lineHeight = 15.sp,
            color = Muted
        )
        Spacer(Modifier.height(16.dp))
    }

    if (picking) {
        NomineePickerDialog(
            title = stringResource(R.string.picker_title_property),
            selected = nomineeId,
            onDismiss = { picking = false },
            onPick = {
                nomineeId = it
                picking = false
            }
        )
    }
}

private fun displayNameOf(context: Context, uri: Uri): String {
    val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) return cursor.getString(index)
    }
    return uri.lastPathSegment ?: "document"
}
