package com.minimo.launcher.data.usecase

import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.data.entities.AppInfoEntity
import com.minimo.launcher.utils.AppUtils
import com.minimo.launcher.utils.InstalledApp
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateAllAppsUseCase @Inject constructor(
    private val appUtils: AppUtils,
    private val appInfoDao: AppInfoDao
) {
    suspend operator fun invoke() {
        val installedApps = appUtils.getInstalledApps()
        val dbApps = appInfoDao.getAllAppsFlow().first()

        updateExistingAppsInDb(installedApps, dbApps)
        addNewAppsToDb(installedApps, dbApps)
    }

    private suspend fun updateExistingAppsInDb(
        installedApps: List<InstalledApp>,
        dbApps: List<AppInfoEntity>
    ) {
        val installedAppsMap = installedApps.associateBy { it.packageName }
        dbApps.forEach { dbApp ->
            if (installedAppsMap.containsKey(dbApp.packageName)) {
                // Update the app if it exists in the installed apps list
                val installedApp = installedAppsMap[dbApp.packageName]
                if (installedApp != null && dbApp.appName != installedApp.appName) {
                    appInfoDao.addApps(dbApp.copy(appName = installedApp.appName))
                }
            } else {
                // Delete the app if it is not in the installed apps list
                appInfoDao.deleteApp(dbApp.packageName)
            }
        }
    }

    private suspend fun addNewAppsToDb(
        installedApps: List<InstalledApp>,
        dbApps: List<AppInfoEntity>
    ) {
        val dbAppsPackages = dbApps.map { it.packageName }
        installedApps.forEach { installedApp ->
            if (installedApp.packageName !in dbAppsPackages) {
                appInfoDao.addApps(
                    AppInfoEntity(
                        packageName = installedApp.packageName,
                        appName = installedApp.appName,
                        alternateAppName = installedApp.appName,
                        isFavourite = false,
                        isHidden = false
                    )
                )
            }
        }
    }
}