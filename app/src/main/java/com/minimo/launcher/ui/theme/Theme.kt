package com.minimo.launcher.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.minimo.launcher.utils.AndroidUtils

private val DarkColorScheme = darkColorScheme()

private val LightColorScheme = lightColorScheme()

private val BlackColorScheme = darkColorScheme(
    onSurface = Color.White,
    surface = Color.Black
)

@Composable
fun AppTheme(
    themeMode: ThemeMode,
    useDynamicTheme: Boolean,
    statusBarVisible: Boolean,
    content: @Composable () -> Unit
) {
    val isDynamicTheme = useDynamicTheme && AndroidUtils.isDynamicThemeSupported()
    var isLightTheme = false
    val colorScheme = when (themeMode) {
        ThemeMode.System -> if (isSystemInDarkTheme()) {
            if (isDynamicTheme) {
                dynamicDarkColorScheme(LocalContext.current)
            } else {
                DarkColorScheme
            }
        } else {
            isLightTheme = true

            if (isDynamicTheme) {
                dynamicLightColorScheme(LocalContext.current)
            } else {
                LightColorScheme
            }
        }

        ThemeMode.Dark -> if (isDynamicTheme) {
            dynamicDarkColorScheme(LocalContext.current)
        } else {
            DarkColorScheme
        }

        ThemeMode.Light -> {
            isLightTheme = true

            if (isDynamicTheme) {
                dynamicLightColorScheme(LocalContext.current)
            } else {
                LightColorScheme
            }
        }

        ThemeMode.Black -> if (isDynamicTheme) {
            dynamicDarkColorScheme(LocalContext.current).copy(
                onSurface = Color.White,
                surface = Color.Black
            )
        } else {
            BlackColorScheme
        }
    }

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