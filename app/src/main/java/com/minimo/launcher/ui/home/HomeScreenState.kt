package com.minimo.launcher.ui.home

import androidx.compose.ui.text.style.TextAlign
import com.minimo.launcher.ui.entities.AppInfo

data class HomeScreenState(
    val initialLoaded: Boolean = false,
    val favouriteApps: List<AppInfo> = emptyList(),
    val allApps: List<AppInfo> = emptyList(),
    val filteredAllApps: List<AppInfo> = emptyList(),
    val isBottomSheetExpanded: Boolean = false,
    val renameAppDialog: AppInfo? = null,
    val searchText: String = "",
    val appsTextAlign: TextAlign = TextAlign.Start
)
