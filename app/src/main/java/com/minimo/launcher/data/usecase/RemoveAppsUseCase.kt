package com.minimo.launcher.data.usecase

import com.minimo.launcher.data.AppInfoDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveAppsUseCase @Inject constructor(
    private val appInfoDao: AppInfoDao
) {
    suspend operator fun invoke(packageName: String, userHandle: Int) {
        appInfoDao.deleteAppByPackage(packageName, userHandle)
    }
}