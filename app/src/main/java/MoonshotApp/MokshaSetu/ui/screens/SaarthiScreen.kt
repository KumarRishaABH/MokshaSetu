package MoonshotApp.MokshaSetu.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import MoonshotApp.MokshaSetu.R
import MoonshotApp.MokshaSetu.data.DemoRepository
import MoonshotApp.MokshaSetu.data.SaarthiEngine
import MoonshotApp.MokshaSetu.data.VoiceCapture
import MoonshotApp.MokshaSetu.ui.noRippleClickable
import MoonshotApp.MokshaSetu.ui.theme.Gold
import MoonshotApp.MokshaSetu.ui.theme.GoldSoft
import MoonshotApp.MokshaSetu.ui.theme.LineC
import MoonshotApp.MokshaSetu.ui.theme.Navy

@Composable
fun SaarthiScreen() {
    var input by remember { mutableStateOf("") }
    var voiceHint by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    fun respondTo(text: String) {
        DemoRepository.appendUserMessage(text)
        val replyRes = SaarthiEngine.replyRes(SaarthiEngine.classify(text))
        DemoRepository.appendAiMessage(replyRes)
    }

    LaunchedEffect(DemoRepository.chat.size) {
        if (DemoRepository.chat.isNotEmpty()) listState.animateScrollToItem(DemoRepository.chat.size - 1)
    }

    LaunchedEffect(VoiceCapture.pendingText.value) {
        VoiceCapture.pendingText.value?.let { spoken ->
            if (spoken.isNotBlank()) respondTo(spoken) else voiceHint = true
            VoiceCapture.pendingText.value = null
        }
    }

    Column(modifier = Modifier.fillMaxSize().imePadding().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            LanguageChips()
        }
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f).padding(top = 12.dp)
        ) {
            items(DemoRepository.chat) { message -> ChatBubble(message) }
        }
        SuggestionRow(
            onSuggestion = { suggestion -> respondTo(suggestion) }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(1.dp, LineC), RoundedCornerShape(30.dp))
                .padding(start = 14.dp, end = 6.dp, top = 2.dp, bottom = 2.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    voiceHint = false
                },
                placeholder = { Text(stringResource(R.string.saarthi_input_hint), fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Gold)
                    .noRippleClickable {
                        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                        }
                        val activity = context as? Activity
                        if (activity == null) {
                            voiceHint = true
                        } else {
                            runCatching {
                                @Suppress("WrongStartActivityForResultUsage")
                                activity.startActivityForResult(recognizerIntent, VoiceCapture.REQUEST_CODE)
                            }.onFailure { voiceHint = true }
                        }
                    }
            ) {
                Text("🎙", fontSize = 15.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.saarthi_send),
                color = Navy,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .noRippleClickable({
                        if (input.isNotBlank()) {
                            respondTo(input.trim())
                            input = ""
                        }
                    })
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            )
        }
        if (voiceHint) {
            Text(
                stringResource(R.string.voice_unavailable),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LanguageChips() {
    val languages = listOf(
        stringResource(R.string.lang_hindi),
        stringResource(R.string.lang_english),
        stringResource(R.string.lang_tamil),
        stringResource(R.string.lang_bengali),
        stringResource(R.string.lang_telugu),
        stringResource(R.string.lang_marathi),
        stringResource(R.string.lang_more)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        languages.forEachIndexed { index, lang ->
            val active = index == 0
            Surface(
                shape = CircleShape,
                color = if (active) Navy else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (active) Navy else LineC)
            ) {
                Text(
                    lang,
                    fontSize = 9.sp,
                    color = if (active) GoldSoft else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: MoonshotApp.MokshaSetu.data.ChatMessage) {
    val text = message.textRes?.let { stringResource(it) } ?: message.text.orEmpty()
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.fromAi) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (message.fromAi) MaterialTheme.colorScheme.surface else Navy,
            border = if (message.fromAi) BorderStroke(1.dp, LineC) else null,
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            Text(
                text,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = if (message.fromAi) MaterialTheme.colorScheme.onSurface else androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun SuggestionRow(onSuggestion: (String) -> Unit) {
    val suggestions = listOf(
        stringResource(R.string.saarthi_suggest_1),
        stringResource(R.string.saarthi_suggest_2),
        stringResource(R.string.saarthi_suggest_3)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        suggestions.forEach { suggestion ->
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.noRippleClickable { onSuggestion(suggestion) }
            ) {
                Text(
                    suggestion,
                    fontSize = 9.5.sp,
                    color = Navy,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                )
            }
        }
    }
}
