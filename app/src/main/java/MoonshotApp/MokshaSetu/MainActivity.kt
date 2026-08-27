package MoonshotApp.MokshaSetu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
}
