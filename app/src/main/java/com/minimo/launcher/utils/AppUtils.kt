package com.minimo.launcher.utils

import android.content.Context
import android.content.pm.LauncherApps
import android.os.UserManager
import com.minimo.launcher.data.entities.AppInfoEntity
import com.minimo.launcher.ui.entities.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppUtils @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    fun getInstalledApps(): List<InstalledApp> {
        val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

        val selfPackageName = context.packageName

        val installedApps = mutableListOf<InstalledApp>()

        for (profile in userManager.userProfiles) {
            val activities = launcherApps.getActivityList(null, profile)
            for (activity in activities) {
                val appName = activity.label.toString()
                val packageName = activity.componentName.packageName
                val className = activity.componentName.className

                /*
                * Ignore the self package name.
                * Add all user apps to the list.
                * */
                if (packageName.contains(selfPackageName, true).not()) {
                    installedApps.add(
                        InstalledApp(
                            appName = appName,
                            packageName = packageName,
                            className = className,
                            userHandle = profile.hashCode()
                        )
                    )
                }
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
            className = className,
            userHandle = userHandle,
            appName = appName,
            alternateAppName = alternateAppName,
            isFavourite = isFavourite,
            isHidden = isHidden
        )
    }
}

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val className: String,
    val userHandle: Int
)