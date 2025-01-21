package com.minimo.launcher.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.data.PreferenceHelper
import com.minimo.launcher.data.usecase.UpdateAllAppsUseCase
import com.minimo.launcher.ui.entities.AppInfo
import com.minimo.launcher.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val updateAllAppsUseCase: UpdateAllAppsUseCase,
    private val appInfoDao: AppInfoDao,
    private val appUtils: AppUtils,
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {
    private val _allAppsFlow = MutableStateFlow<List<AppInfo>>(emptyList())
    val allAppsFlow: StateFlow<List<AppInfo>> = _allAppsFlow

    val favouriteAppsFlow =
        _allAppsFlow.map { allApps ->
            allApps.filter { it.isFavourite }
        }

    init {
        viewModelScope.launch {
            updateAllAppsUseCase.invoke()

            appInfoDao.getAllNonHiddenAppsFlow()
                .collect { appInfoList ->
                    _allAppsFlow.value = appUtils.mapToAppInfo(appInfoList)
                }
        }
    }

    fun onToggleFavouriteAppClick(appInfo: AppInfo) {
        viewModelScope.launch {
            if (appInfo.isFavourite) {
                appInfoDao.removeAppFromFavourite(appInfo.packageName)
            } else {
                appInfoDao.addAppToFavourite(appInfo.packageName)
            }
        }
    }

    fun setIntroCompleted() {
        viewModelScope.launch {
            preferenceHelper.setIsIntroCompleted(true)
        }
    }
}