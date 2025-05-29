package com.minimo.launcher.ui.main

import com.minimo.launcher.ui.theme.ThemeMode

data class MainState(
    val themeMode: ThemeMode = ThemeMode.System,
    val statusBarVisible: Boolean = true,
    val useDynamicTheme: Boolean = false,
    val blackTheme: Boolean = false
)
