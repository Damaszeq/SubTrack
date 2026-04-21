package pl.lab2.subtrack.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BluePrimary = Color(0xFF0056D2)
val DarkSeaGreen = Color(0xFF004B41)
val JuicyGreen = Color(0xFF2ECC71)
val PalmMint = Color(0xFFE8F5E9)
val White = Color(0xFFFFFFFF)
val ErrorRed = Color(0xFFBA1A1A)
val LabelGray = Color(0xFF747978)


val OffWhite = Color(0xFFFBFDFB)
val TextGray = Color(0xFF444746)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = White,

    primaryContainer = BluePrimary,
    onPrimaryContainer = DarkSeaGreen,

    secondary = JuicyGreen,
    onSecondary = White,

    background = OffWhite,
    surface = White,
    onSurface = TextGray,

    surfaceVariant = Color(0xFFF2F2F2),
    onSurfaceVariant = LabelGray,

    error = ErrorRed,
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