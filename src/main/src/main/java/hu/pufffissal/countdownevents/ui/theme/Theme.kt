package hu.pufffissal.countdownevents.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val Scheme = darkColorScheme(
    background = Bg,
    surface = Surface,
    primary = TextPrimary,
    secondary = TextSecondary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun CountdownTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Scheme,
        typography = Typography,
        content = content
    )
}

