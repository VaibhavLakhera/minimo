package com.minimo.launcher.ui.hidden_apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.ui.entities.AppInfo
import com.minimo.launcher.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HiddenAppsViewModel @Inject constructor(
    private val appInfoDao: AppInfoDao,
    private val appUtils: AppUtils
) : ViewModel() {
    private val _state = MutableStateFlow(HiddenAppsState())
    val state: StateFlow<HiddenAppsState> = _state

    init {
        viewModelScope.launch {
            appInfoDao.getAllAppsFlow()
                .collect { appInfoList ->
                    val allApps = appUtils.mapToAppInfo(appInfoList)
                    _state.update {
                        state.value.copy(
                            allApps = allApps,
                            filteredAllApps = appUtils.getAppsWithSearch(
                                searchText = _state.value.searchText,
                                apps = allApps
                            ),
                        )
                    }
                }
        }
    }

    fun onToggleHiddenAppClick(appInfo: AppInfo) {
        viewModelScope.launch {
            if (appInfo.isHidden) {
                appInfoDao.removeAppFromHidden(appInfo.packageName)
            } else {
                appInfoDao.addAppToHidden(appInfo.packageName)
            }
        }
    }

    fun onSearchTextChange(searchText: String) {
        _state.update {
            _state.value.copy(
                searchText = searchText,
                filteredAllApps = appUtils.getAppsWithSearch(
                    searchText = searchText,
                    apps = _state.value.allApps
                )
            )
        }
    }
}