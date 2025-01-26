package com.minimo.launcher.ui.intro

import com.minimo.launcher.ui.entities.AppInfo

data class IntroState(
    val allApps: List<AppInfo> = emptyList(),
    val filteredAllApps: List<AppInfo> = emptyList(),
    val searchText: String = "",
    val minimumFavouriteAdded: Boolean = false
)
