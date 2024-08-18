package com.minimo.launcher.ui.hidden_apps

import com.minimo.launcher.ui.entities.AppInfo

data class HiddenAppsState(
    val initialLoaded: Boolean,
    val hiddenApps: List<AppInfo>
)