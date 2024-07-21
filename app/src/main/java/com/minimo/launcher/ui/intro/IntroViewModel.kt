package com.minimo.launcher.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.data.usecase.UpdateAllAppsUseCase
import com.minimo.launcher.ui.entities.AppInfo
import com.minimo.launcher.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val updateAllAppsUseCase: UpdateAllAppsUseCase,
    private val appInfoDao: AppInfoDao,
    private val appUtils: AppUtils
) : ViewModel() {
    init {
        viewModelScope.launch {
            updateAllAppsUseCase.invoke()

            appInfoDao.getAllNonHiddenAppsFlow()
                .collect { appInfoList ->
                    _allAppsFlow.value = appUtils.mapToAppInfo(appInfoList)
                }
        }
    }

    private val _allAppsFlow = MutableStateFlow<List<AppInfo>>(emptyList())
    val allAppsFlow: StateFlow<List<AppInfo>> = _allAppsFlow

    fun onToggleFavouriteAppClick(appInfo: AppInfo) {
        viewModelScope.launch {
            if (appInfo.isFavourite) {
                appInfoDao.removeAppFromFavourite(appInfo.packageName)
            } else {
                appInfoDao.addAppToFavourite(appInfo.packageName)
            }
        }
    }
}