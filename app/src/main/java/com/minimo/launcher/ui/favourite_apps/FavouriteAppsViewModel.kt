package com.minimo.launcher.ui.favourite_apps

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
class FavouriteAppsViewModel @Inject constructor(
    private val appInfoDao: AppInfoDao,
    private val appUtils: AppUtils
) : ViewModel() {
    private val _state = MutableStateFlow(FavouriteAppsState())
    val state: StateFlow<FavouriteAppsState> = _state

    init {
        viewModelScope.launch {
            appInfoDao.getAllNonHiddenAppsFlow()
                .collect { appInfoList ->
                    val allApps = appUtils.mapToAppInfo(appInfoList)
                    _state.value = _state.value.copy(
                        allApps = allApps,
                        filteredAllApps = appUtils.getAppsWithSearch(
                            searchText = _state.value.searchText,
                            apps = allApps
                        ),
                    )
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