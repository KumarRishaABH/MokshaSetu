package MoonshotApp.MokshaSetu.ui.screens.nominee

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import MoonshotApp.MokshaSetu.data.DeathCertificate
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.Fixtures
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

@Composable
fun DeathCertificateScreen(onSubmit: () -> Unit) {
    val context = LocalContext.current
    val nomineeName = DemoRepository.nomineeProfile?.name.orEmpty()
    val registeredName = DemoRepository.vaultOwnerName ?: Fixtures.deathCertificate.deceasedName

    var registrationNo by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(Fixtures.states.first()) }
    var deceasedName by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf<String?>(null) }
    var stateMenuOpen by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) fileName = displayNameOf(context, uri)
    }

    val canSubmit = registrationNo.isNotBlank() && deceasedName.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.cert_condolence_fmt, nomineeName.substringBefore(' ')),
            fontSize = 12.5.sp,
            lineHeight = 19.sp,
            color = Navy,
            fontWeight = FontWeight.SemiBold
        )
        BannerCard(GreyBg, stringResource(R.string.cert_intro))

        SectionTitle(stringResource(R.string.cert_section_record))
        VirasatTextField(
            value = registrationNo,
            onValueChange = { registrationNo = it },
            label = stringResource(R.string.cert_reg_no_label)
        )

        Box {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GreyBg,
                modifier = Modifier.fillMaxWidth().noRippleClickable { stateMenuOpen = true }
            ) {
                Row(modifier = Modifier.padding(13.dp).fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.cert_state_label),
                            fontSize = 9.5.sp,
                            letterSpacing = 0.8.sp,
                            color = Muted
                        )
                        Text(state, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = Navy)
                    }
                    Text("▼", fontSize = 10.sp, color = Navy)
                }
            }
            DropdownMenu(expanded = stateMenuOpen, onDismissRequest = { stateMenuOpen = false }) {
                Fixtures.states.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 12.5.sp) },
                        onClick = {
                            state = option
                            stateMenuOpen = false
                        }
                    )
                }
            }
        }

        VirasatTextField(
            value = deceasedName,
            onValueChange = { deceasedName = it },
            label = stringResource(R.string.cert_deceased_label)
        )

        SectionTitle(stringResource(R.string.cert_section_doc))
        OutlineButton(
            text = if (fileName == null) {
                stringResource(R.string.cert_pick_doc)
            } else {
                stringResource(R.string.cert_change_doc)
            }
        ) { picker.launch("*/*") }

        fileName?.let { name ->
            Surface(shape = RoundedCornerShape(12.dp), color = GreenBg, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(12.dp)) {
                    Text("📄", fontSize = 14.sp)
                    Column {
                        Text(name, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Navy)
                        Text(stringResource(R.string.cert_doc_attached), fontSize = 9.5.sp, color = GreenOk)
                    }
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GreyBg,
            modifier = Modifier.fillMaxWidth().noRippleClickable {
                registrationNo = Fixtures.deathCertificate.registrationNo
                state = Fixtures.deathCertificate.state
                deceasedName = registeredName
            }
        ) {
            Column(Modifier.padding(11.dp)) {
                Text(
                    stringResource(R.string.cert_demo_label),
                    fontSize = 9.sp,
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    stringResource(
                        R.string.cert_demo_value_fmt,
                        Fixtures.deathCertificate.registrationNo,
                        Fixtures.deathCertificate.state
                    ),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    stringResource(R.string.cert_demo_name_fmt, registeredName),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Navy
                )
                Text(stringResource(R.string.cert_demo_hint), fontSize = 9.5.sp, color = Muted)
            }
        }

        GoldButton(stringResource(R.string.cert_submit), enabled = canSubmit) {
            DemoRepository.setDeathCertificate(
                DeathCertificate(
                    registrationNo = registrationNo.trim(),
                    state = state,
                    issuedOn = Fixtures.deathCertificate.issuedOn,
                    deceasedName = deceasedName.trim(),
                    fileName = fileName
                )
            )
            onSubmit()
        }
        Text(
            stringResource(R.string.cert_footer),
            fontSize = 9.5.sp,
            lineHeight = 15.sp,
            color = Muted
        )
        Spacer(Modifier.height(16.dp))
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
