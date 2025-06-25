package com.minimo.launcher.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.minimo.launcher.data.entities.AppInfoEntity
import com.minimo.launcher.ui.entities.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppUtils @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    fun getInstalledApps(): List<InstalledApp> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
        val selfPackageName = context.packageName

        val installedApps = mutableListOf<InstalledApp>()
        for (resolveInfo in activities) {
            val activityInfo = resolveInfo.activityInfo
            val packageName = activityInfo.packageName
            val className = activityInfo.name
            val appName = resolveInfo.loadLabel(pm).toString()

            if (packageName.contains(selfPackageName, true).not()) {
                installedApps.add(
                    InstalledApp(
                        appName = appName,
                        packageName = packageName,
                        className = className
                    )
                )
            }
        }

        return installedApps
    }

    fun mapToAppInfo(
        entities: List<AppInfoEntity>
    ): List<AppInfo> {
        return entities.map { it.toAppInfo() }
            .sortedBy { it.alternateAppName.lowercase() }
    }

    fun getAppsWithSearch(searchText: String, apps: List<AppInfo>): List<AppInfo> {
        if (searchText.isBlank()) return apps

        return apps.filter { appInfo ->
            appInfo.name.contains(searchText, ignoreCase = true)
        }
    }

    private fun AppInfoEntity.toAppInfo(): AppInfo {
        return AppInfo(
            packageName = packageName,
            appName = appName,
            className = className,
            alternateAppName = alternateAppName,
            isFavourite = isFavourite,
            isHidden = isHidden
        )
    }
}

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val className: String
)