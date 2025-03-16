package com.minimo.launcher.ui.settings.appearance

import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.Constants
import com.minimo.launcher.utils.HomeAppsAlignment
import com.minimo.launcher.utils.HomeClockAlignment

data class AppearanceState(
    val themeMode: ThemeMode? = null,
    val homeAppsAlignment: HomeAppsAlignment? = null,
    val homeClockAlignment: HomeClockAlignment? = null,
    val showHomeClock: Boolean = false,
    val showStatusBar: Boolean = true,
    val homeTextSize: Float = Constants.DEFAULT_HOME_TEXT_SIZE.toFloat(),
    val autoOpenKeyboardAllApps: Boolean = false
)
