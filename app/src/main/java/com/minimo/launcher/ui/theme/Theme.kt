package com.minimo.launcher.ui.theme

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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
    blackTheme: Boolean,
    useDynamicTheme: Boolean,
    statusBarVisible: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isDynamicTheme = useDynamicTheme && AndroidUtils.isDynamicThemeSupported()
    var isLightTheme = false

    fun getDarkTheme(context: Context): ColorScheme {
        return if (isDynamicTheme) {
            if (blackTheme) {
                dynamicDarkColorScheme(context).copy(
                    onSurface = Color.White,
                    surface = Color.Black
                )
            } else {
                dynamicDarkColorScheme(context)
            }
        } else {
            if (blackTheme) {
                BlackColorScheme
            } else {
                DarkColorScheme
            }
        }
    }

    fun getLightTheme(context: Context): ColorScheme {
        return if (isDynamicTheme) {
            dynamicLightColorScheme(context)
        } else {
            LightColorScheme
        }
    }

    val colorScheme = when (themeMode) {
        ThemeMode.System -> if (isSystemInDarkTheme()) {
            getDarkTheme(context)
        } else {
            isLightTheme = true
            getLightTheme(context)
        }

        ThemeMode.Dark -> getDarkTheme(context)

        ThemeMode.Light -> {
            isLightTheme = true
            getLightTheme(context)
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