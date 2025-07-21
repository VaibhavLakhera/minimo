package com.minimo.launcher.data.usecase

import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.data.entities.AppInfoEntity
import com.minimo.launcher.utils.AppUtils
import com.minimo.launcher.utils.InstalledApp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddUpdateAppsUseCase @Inject constructor(
    private val appUtils: AppUtils,
    private val appInfoDao: AppInfoDao
) {
    suspend operator fun invoke(packageName: String) {
        val installedApps = appUtils.getInstalledApps(packageName)
        val dbApps = appInfoDao.getAppsByPackageName(packageName)
        addUpdateAppsInDb(installedApps, dbApps)

        // Remove the apps by className + packageName which exists in DB but not in installed apps
        // This could happen if any component was disabled or removed
        val installedAppIds = installedApps.map { it.id }
        val removedApps = dbApps.filterNot { installedAppIds.contains(it.id) }
        for (removedApp in removedApps) {
            appInfoDao.deleteAppByClassAndPackage(
                className = removedApp.className,
                packageName = removedApp.packageName,
                userHandle = removedApp.userHandle
            )
        }
    }

    private suspend fun addUpdateAppsInDb(
        installedApps: List<InstalledApp>,
        dbApps: List<AppInfoEntity>
    ) {
        val dbAppsMap = dbApps.associateBy { it.id }
        val addApps = mutableListOf<AppInfoEntity>()

        for (installedApp in installedApps) {
            if (dbAppsMap.containsKey(installedApp.id)) {
                // Update the app if it exists in the database
                val dbApp = dbAppsMap[installedApp.id]
                if (dbApp != null) {
                    addApps.add(dbApp.copy(appName = installedApp.appName))
                }
            } else {
                // Add the app if it does not exists in the database
                addApps.add(
                    AppInfoEntity(
                        packageName = installedApp.packageName,
                        className = installedApp.className,
                        userHandle = installedApp.userHandle,
                        appName = installedApp.appName,
                        alternateAppName = "",
                        isFavourite = false,
                        isHidden = false,
                    )
                )
            }
        }

        if (addApps.isNotEmpty()) {
            appInfoDao.addApps(addApps)
        }
    }
}