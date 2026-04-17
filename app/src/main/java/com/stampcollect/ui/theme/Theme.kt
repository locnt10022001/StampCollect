package com.stampcollect.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StampLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = BgPrimary,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSection,
    onSurfaceVariant = TextSecondary,
    outline = OutlineVariant,
    error = Coral,
    onError = Color.White
)

@Composable
fun StampCollectionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StampLightColorScheme,
        typography = StampTypography,
        content = content
    )
}
