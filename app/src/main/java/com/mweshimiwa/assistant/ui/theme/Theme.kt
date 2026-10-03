package com.mweshimiwa.assistant.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MweshimiwaCyan,
    onPrimary = DarkBackground,
    primaryContainer = MweshimiwaCyanDim,
    onPrimaryContainer = DarkOnSurface,
    secondary = MweshimiwaBlue,
    onSecondary = DarkBackground,
    tertiary = MweshimiwaPurple,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkError,
    onError = DarkBackground
)

private val LightColorScheme = lightColorScheme(
    primary = MweshimiwaCyanDim,
    onPrimary = LightBackground,
    primaryContainer = MweshimiwaCyan,
    onPrimaryContainer = LightOnSurface,
    secondary = MweshimiwaBlue,
    onSecondary = LightBackground,
    tertiary = MweshimiwaPurple,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = LightError,
    onError = LightBackground
)

@Composable
fun MweshimiwaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MweshimiwaTypography,
        content = content
    )
}
