package com.minimo.launcher.data.usecase

import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.data.entities.AppInfoEntity
import com.minimo.launcher.utils.AppUtils
import com.minimo.launcher.utils.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAllAppsUseCase @Inject constructor(
    private val appUtils: AppUtils,
    private val appInfoDao: AppInfoDao
) {
    suspend operator fun invoke() {
        val installedApps = withContext(Dispatchers.IO) {
            appUtils.getInstalledApps()
        }
        val dbApps = appInfoDao.getAllAppsFlow().first()

        updateExistingAppsInDb(installedApps, dbApps)
        addNewAppsToDb(installedApps, dbApps)
    }

    private suspend fun updateExistingAppsInDb(
        installedApps: List<InstalledApp>,
        dbApps: List<AppInfoEntity>
    ) {
        val installedAppsMap = installedApps.associateBy { it.className + it.userHandle }
        for (dbApp in dbApps) {
            if (installedAppsMap.containsKey(dbApp.className + dbApp.userHandle)) {
                // Update the app if it exists in the installed apps list
                val installedApp = installedAppsMap[dbApp.className + dbApp.userHandle]
                if (installedApp != null && dbApp.appName != installedApp.appName) {
                    appInfoDao.addApps(dbApp.copy(appName = installedApp.appName))
                }
            } else {
                // Delete the app if it is not in the installed apps list
                appInfoDao.deleteApp(dbApp.className)
            }
        }
    }

    private suspend fun addNewAppsToDb(
        installedApps: List<InstalledApp>,
        dbApps: List<AppInfoEntity>
    ) {
        val dbAppsPackages = dbApps.map { it.className + it.userHandle }
        for (installedApp in installedApps) {
            if (installedApp.className + installedApp.userHandle !in dbAppsPackages) {
                appInfoDao.addApps(
                    AppInfoEntity(
                        packageName = installedApp.packageName,
                        className = installedApp.className,
                        userHandle = installedApp.userHandle,
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