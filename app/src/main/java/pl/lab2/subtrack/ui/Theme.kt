package pl.lab2.subtrack.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BrandIndigo = Color(0xFF4F46E5)
val IceBlue = Color(0xFFEEF2FF)
val DeepSlate = Color(0xFF1E1B4B)
val EmeraldGreen = Color(0xFF10B981)
val White = Color(0xFFFFFFFF)
val PremiumRed = Color(0xFFEF4444)
val CoolGray = Color(0xFF6B7280)
val AppBackground = Color(0xFFF9FAFB)
val CardStroke = Color(0xFFE5E7EB)

private val LightColorScheme = lightColorScheme(
    primary = BrandIndigo,
    onPrimary = White,

    primaryContainer = IceBlue,
    onPrimaryContainer = DeepSlate,

    secondary = EmeraldGreen,
    onSecondary = White,

    background = AppBackground,
    onBackground = DeepSlate,

    surface = White,
    onSurface = DeepSlate,

    surfaceVariant = IceBlue,
    onSurfaceVariant = CoolGray,

    error = PremiumRed,
    onError = White
)

@Composable
fun SubTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}