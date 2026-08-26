package MoonshotApp.MokshaSetu.data

import androidx.compose.runtime.mutableStateOf

object VoiceCapture {
    var pendingText = mutableStateOf<String?>(null)
    const val REQUEST_CODE = 4201
}
