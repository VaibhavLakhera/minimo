package com.minimo.launcher.ui.entities

data class AppInfo(
    val packageName: String,
    val appName: String,
    val alternateAppName: String,
    val isFavourite: Boolean,
    val isHidden: Boolean
) {
    val name: String
        get() = alternateAppName.ifEmpty { appName }
}