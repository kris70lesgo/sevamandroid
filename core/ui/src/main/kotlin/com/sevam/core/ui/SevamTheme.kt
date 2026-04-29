package com.sevam.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object SevamColors {
    val Orange = Color(0xFFFF7A1A)
    val OrangeContainer = Color(0xFFFFF1E6)
    val Navy = Color(0xFF173A77)
    val NavyMuted = Color(0xFF5F79A6)
    val Success = Color(0xFF12B76A)
    val SuccessContainer = Color(0xFFE8FFF3)
    val Warning = Color(0xFFF79009)
    val SurfaceAlt = Color(0xFFF7F8FC)
    val Border = Color(0xFFE6EAF2)
    val TextSecondary = Color(0xFF667085)
}

private val LightColors = lightColorScheme(
    primary = SevamColors.Orange,
    onPrimary = Color.White,
    secondary = SevamColors.Navy,
    onSecondary = Color.White,
    tertiary = SevamColors.Success,
    background = Color(0xFFF8FAFD),
    surface = Color.White,
    onSurface = Color(0xFF101828),
    surfaceVariant = SevamColors.SurfaceAlt,
    outline = SevamColors.Border,
)

private val DarkColors = darkColorScheme(
    primary = SevamColors.Orange,
    secondary = Color(0xFF9EBAEA),
    tertiary = SevamColors.Success,
)

@Composable
fun SevamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
