package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CateringColorScheme = darkColorScheme(
    primary = CateringOrange,
    onPrimary = Color.White,
    primaryContainer = CateringOrangeContainer,
    onPrimaryContainer = CateringOrangeLight,
    secondary = CateringOrangeLight,
    onSecondary = Color.White,
    secondaryContainer = CateringCardElevated,
    onSecondaryContainer = CateringTextPrimary,
    background = CateringDarkNavy,
    onBackground = CateringTextPrimary,
    surface = CateringCardNavy,
    onSurface = CateringTextPrimary,
    surfaceVariant = CateringCardElevated,
    onSurfaceVariant = CateringTextSecondary,
    outline = CateringBorder,
    error = CateringDangerRed,
    onError = Color.White,
    errorContainer = CateringDangerRedBg,
    onErrorContainer = CateringDangerRed
)

@Composable
fun CateringDisplayManagerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CateringColorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    CateringDisplayManagerTheme(content = content)
}
