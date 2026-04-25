package br.com.monitorfan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ColorSchemeMonitorFan = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = WhiteSoft,
    secondary = BluePrimary,
    onSecondary = WhiteSoft,
    tertiary = BorderSoft,
    background = BlueDark,
    onBackground = WhiteSoft,
    surface = CardColor,
    onSurface = WhiteSoft,
    surfaceVariant = FieldColor,
    onSurfaceVariant = GrayText,
    outline = BorderSoft
)

@Composable
fun MonitorFanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorSchemeMonitorFan,
        typography = Typography,
        content = content
    )
}
