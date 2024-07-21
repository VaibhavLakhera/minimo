package com.minimo.launcher.utils

import android.content.Context
import android.content.Intent
import com.minimo.launcher.data.entities.AppInfoEntity
import com.minimo.launcher.ui.entities.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppUtils @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    fun getInstalledApps(): List<InstalledApp> {
        val packageManager = context.packageManager

        val intent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        val applications = packageManager.queryIntentActivities(intent, 0)
        val selfPackageName = context.packageName

        val installedApps = mutableListOf<InstalledApp>()
        applications.forEach { application ->
            val appName = application.loadLabel(packageManager).toString()
            val packageName = application.activityInfo.packageName

            /*
            * Ignore the self package name.
            * Add all user apps to the list.
            * */
            if (packageName.contains(selfPackageName, true).not()) {
                installedApps.add(
                    InstalledApp(
                        appName = appName,
                        packageName = packageName
                    )
                )
            }
        }

        return installedApps
    }

    fun mapToAppInfo(
        entities: List<AppInfoEntity>,
        includeSettings: Boolean = false
    ): List<AppInfo> {
        val appInfos = entities.map { it.toAppInfo() }
        if (includeSettings) {
            val settingsAppInfo = AppInfo(
                packageName = "",
                appName = "Minimo Settings",
                alternateAppName = "Minimo Settings",
                isFavourite = false,
                isHidden = false,
                isLauncherSettings = true
            )
            return (appInfos + settingsAppInfo).sortedBy { it.alternateAppName.lowercase() }
        } else {
            return appInfos.sortedBy { it.alternateAppName.lowercase() }
        }
    }

    private fun AppInfoEntity.toAppInfo(): AppInfo {
        return AppInfo(
            packageName = packageName,
            appName = appName,
            alternateAppName = alternateAppName,
            isFavourite = isFavourite,
            isHidden = isHidden,
            isLauncherSettings = false
        )
    }
}

data class InstalledApp(
    val appName: String,
    val packageName: String
)