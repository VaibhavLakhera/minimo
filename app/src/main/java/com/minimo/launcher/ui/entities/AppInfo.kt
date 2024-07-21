package com.minimo.launcher.ui.entities

import com.minimo.launcher.data.entities.AppInfoEntity

data class AppInfo(
    val packageName: String,
    val appName: String,
    val alternateAppName: String,
    val isFavourite: Boolean,
    val isHidden: Boolean,
    val isLauncherSettings: Boolean = false
) {
    val name: String
        get() = alternateAppName.ifEmpty { appName }
}

object AppInfoMapper {
    fun toAppInfo(
        entity: AppInfoEntity,
        isLauncherSettings: Boolean = false
    ): AppInfo {
        return AppInfo(
            packageName = entity.packageName,
            appName = entity.appName,
            alternateAppName = entity.alternateAppName,
            isFavourite = entity.isFavourite,
            isHidden = entity.isHidden,
            isLauncherSettings = isLauncherSettings
        )
    }
}