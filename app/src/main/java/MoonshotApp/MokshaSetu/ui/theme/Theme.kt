package MoonshotApp.MokshaSetu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VirasatScheme = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = Navy2,
    onPrimaryContainer = GoldSoft,
    secondary = Gold,
    onSecondary = Navy,
    secondaryContainer = GoldSoft,
    onSecondaryContainer = Navy,
    tertiary = GreenOk,
    onTertiary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Cream,
    onSurfaceVariant = Muted,
    outline = LineC,
    error = RedAlert,
    onError = Color.White,
    errorContainer = RedBg,
    onErrorContainer = RedAlert
)

@Composable
fun MokshaSetuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VirasatScheme,
        typography = Typography,
        content = content
    )
}
