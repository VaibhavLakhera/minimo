package com.minimo.launcher.ui.settings.appearance

import androidx.compose.ui.text.style.TextAlign
import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.HomeClockAlignment

data class AppearanceState(
    val themeMode: ThemeMode? = null,
    val homeAppsAlign: TextAlign? = null,
    val homeClockAlignment: HomeClockAlignment? = null,
    val showHomeClock: Boolean = false
)
