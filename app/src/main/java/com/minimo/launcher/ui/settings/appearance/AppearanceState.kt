package com.minimo.launcher.ui.settings.appearance

import androidx.compose.ui.text.style.TextAlign
import com.minimo.launcher.ui.theme.ThemeMode

data class AppearanceState(
    val themeMode: ThemeMode? = null,
    val homeAppsAlign: TextAlign? = null
)
