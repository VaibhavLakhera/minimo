package com.minimo.launcher.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.data.usecase.UpdateAllAppsUseCase
import com.minimo.launcher.ui.entities.AppInfo
import com.minimo.launcher.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val updateAllAppsUseCase: UpdateAllAppsUseCase,
    private val appInfoDao: AppInfoDao,
    private val appUtils: AppUtils
) : ViewModel() {
    init {
        viewModelScope.launch {
            updateAllAppsUseCase.invoke()

            appInfoDao.getAllNonHiddenAppsFlow()
                .collect { appInfoList ->
                    val allApps = appUtils.mapToAppInfo(appInfoList)
                    _state.update {
                        _state.value.copy(
                            allApps = allApps,
                            filteredAllApps = getAppsWithSearch(
                                searchText = _state.value.searchText,
                                apps = allApps
                            )
                        )
                    }
                }
        }

        viewModelScope.launch {
            appInfoDao.getFavouriteAppsFlow()
                .collect { appInfoList ->
                    _state.update {
                        _state.value.copy(
                            initialLoaded = true,
                            favouriteApps = appUtils.mapToAppInfo(appInfoList)
                        )
                    }
                }
        }
    }

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state

    private val _launchApp = Channel<String>(Channel.BUFFERED)
    val launchApp: Flow<String> = _launchApp.receiveAsFlow()

    fun setBottomSheetExpanded(isExpanded: Boolean) {
        _state.update {
            _state.value.copy(
                isBottomSheetExpanded = isExpanded
            )
        }

        // Clear out the search text when bottom sheet is collapsed
        if (!isExpanded && _state.value.searchText.isNotBlank()) {
            onSearchTextChange("")
        }
    }

    private fun onAddAppToFavouriteClick(packageName: String) {
        viewModelScope.launch {
            appInfoDao.addAppToFavourite(packageName)
        }
    }

    fun onToggleFavouriteAppClick(app: AppInfo) {
        if (app.isFavourite) {
            onRemoveAppFromFavouriteClicked(app.packageName)
        } else {
            onAddAppToFavouriteClick(app.packageName)
        }
    }

    fun onRemoveAppFromFavouriteClicked(packageName: String) {
        viewModelScope.launch {
            appInfoDao.removeAppFromFavourite(packageName)
        }
    }

    fun onLaunchAppClick(info: AppInfo) {
        viewModelScope.launch {
            _launchApp.send(info.packageName)
        }
    }

    fun onHideAppClick(packageName: String) {
        viewModelScope.launch {
            appInfoDao.addAppToHidden(packageName)
        }
    }

    fun onRenameAppClick(app: AppInfo) {
        _state.update {
            _state.value.copy(
                renameAppDialog = app
            )
        }
    }

    fun onRenameApp(newName: String) {
        val app = _state.value.renameAppDialog ?: return
        onDismissRenameAppDialog()
        viewModelScope.launch {
            val name = newName.ifBlank {
                app.appName
            }
            appInfoDao.renameApp(app.packageName, name)
        }
    }

    fun onDismissRenameAppDialog() {
        _state.update {
            _state.value.copy(
                renameAppDialog = null
            )
        }
    }

    fun onSearchTextChange(searchText: String) {
        _state.update {
            _state.value.copy(
                searchText = searchText,
                filteredAllApps = getAppsWithSearch(
                    searchText = searchText,
                    apps = _state.value.allApps
                )
            )
        }
    }

    private fun getAppsWithSearch(searchText: String, apps: List<AppInfo>): List<AppInfo> {
        if (searchText.isBlank()) return apps

        return apps.filter { appInfo ->
            appInfo.name.contains(searchText, ignoreCase = true)
        }
    }
}