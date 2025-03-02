package com.minimo.launcher.ui.favourite_apps

import com.minimo.launcher.ui.entities.AppInfo

data class FavouriteAppsState(
    val allApps: List<AppInfo> = emptyList(),
    val filteredAllApps: List<AppInfo> = emptyList(),
    val searchText: String = "",
)