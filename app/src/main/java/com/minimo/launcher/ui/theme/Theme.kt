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
import androidx.core.view.WindowCompat
import com.minimo.launcher.ui.entities.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    onSurface = Color.White,
    surface = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    onSurface = Color.Black,
    surface = Color.White
)

@Composable
fun AppTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.Auto -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        ThemeMode.Dark -> DarkColorScheme
        ThemeMode.Light -> LightColorScheme
    }
    val darkTheme = colorScheme == DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = darkTheme.not()
            insetsController.isAppearanceLightNavigationBars = darkTheme.not()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}