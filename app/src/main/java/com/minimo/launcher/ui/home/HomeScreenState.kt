package com.minimo.launcher.ui.home

import com.minimo.launcher.ui.entities.AppInfo

data class HomeScreenState(
    val initialLoaded: Boolean,
    val favouriteApps: List<AppInfo>,
    val allApps: List<AppInfo>,
    val isBottomSheetExpanded: Boolean,
    val renameAppDialog: AppInfo? = null
)
