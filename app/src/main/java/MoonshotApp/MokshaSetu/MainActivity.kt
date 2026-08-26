package MoonshotApp.MokshaSetu

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import MoonshotApp.MokshaSetu.data.VoiceCapture
import MoonshotApp.MokshaSetu.ui.VirasatApp
import MoonshotApp.MokshaSetu.ui.theme.MokshaSetuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MokshaSetuTheme {
                VirasatApp()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VoiceCapture.REQUEST_CODE && resultCode == RESULT_OK) {
            VoiceCapture.pendingText.value = data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
        }
    }
}
