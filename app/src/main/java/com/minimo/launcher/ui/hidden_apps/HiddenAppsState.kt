package com.minimo.launcher.ui.hidden_apps

import com.minimo.launcher.ui.entities.AppInfo

data class HiddenAppsState(
    val allApps: List<AppInfo> = emptyList(),
    val hiddenApps: List<AppInfo> = emptyList(),
    val filteredAllApps: List<AppInfo> = emptyList(),
    val searchText: String = "",
    val showAppBarMorePopup: Boolean = false,
    val showHiddenOnly: Boolean = false
)