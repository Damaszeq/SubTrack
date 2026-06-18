package pl.lab2.subtrack.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==========================================
// PALETA KOLORÓW (Jasny motyw)
// ==========================================
val BrandIndigo = Color(0xFF4F46E5)
val IceBlue = Color(0xFFEEF2FF)
val DeepSlate = Color(0xFF1E1B4B)
val EmeraldGreen = Color(0xFF10B981)
val White = Color(0xFFFFFFFF)
val PremiumRed = Color(0xFFEF4444)
val CoolGray = Color(0xFF6B7280)
val AppBackground = Color(0xFFF9FAFB)

val LightCritical = Color(0xFFE50914)
val LightWarning = Color(0xFFFF9800)
val LightInfo = Color(0xFF2196F3)

// ==========================================
// PALETA KOLORÓW (Ciemny motyw)
// ==========================================
val SpaceBackground = Color(0xFF030712)
val NebulaPurple = Color(0xFF1E1B4B)
val NeonCyan = Color(0xFF06B6D4)
val CyberPink = Color(0xFFF43F5E)
val GlassWhiteText = Color(0xFFE2E8F0)
val MutedSlate = Color(0xFF94A3B8)

val DarkCritical = Color(0xFFEF5350)
val DarkWarning = Color(0xFFFFB74D)
val DarkInfo = Color(0xFF64B5F6)

// Konstrukcja motywu jasnego
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

// Konstrukcja motywu ciemnego
private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = SpaceBackground,
    primaryContainer = NebulaPurple,
    onPrimaryContainer = GlassWhiteText,
    secondary = NeonCyan,
    onSecondary = SpaceBackground,
    background = SpaceBackground,
    onBackground = GlassWhiteText,
    surface = Color(0xFF111827),
    onSurface = GlassWhiteText,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = MutedSlate,
    error = CyberPink,
    onError = White
)

// ==========================================
// ROZSZERZENIA DLA KOLORÓW POWIADOMIEŃ
// ==========================================

val ColorScheme.critical: Color
    @Composable get() = if (MaterialTheme.colorScheme.surface == NebulaPurple) DarkCritical else LightCritical

val ColorScheme.warning: Color
    @Composable get() = if (MaterialTheme.colorScheme.surface == NebulaPurple) DarkWarning else LightWarning

val ColorScheme.info: Color
    @Composable get() = if (MaterialTheme.colorScheme.surface == NebulaPurple) DarkInfo else LightInfo


@Composable
fun SubTrackTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}