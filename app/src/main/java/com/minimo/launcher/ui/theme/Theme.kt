package com.minimo.launcher.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

private val DarkColorScheme = darkColorScheme()

private val LightColorScheme = lightColorScheme()

private val BlackColorScheme = darkColorScheme(
    onSurface = Color.White,
    surface = Color.Black
)

@Composable
fun AppTheme(
    themeMode: ThemeMode,
    statusBarVisible: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.System -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        ThemeMode.Dark -> DarkColorScheme
        ThemeMode.Light -> LightColorScheme
        ThemeMode.Black -> BlackColorScheme
    }
    val isLightTheme = colorScheme == LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val surfaceColor = colorScheme.surface.toArgb()
            window.statusBarColor = surfaceColor
            window.navigationBarColor = surfaceColor

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = isLightTheme
            insetsController.isAppearanceLightNavigationBars = isLightTheme

            if (statusBarVisible) {
                insetsController.show(WindowInsetsCompat.Type.statusBars())
            } else {
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
            }

            window.setBackgroundDrawable(surfaceColor.toDrawable())
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}