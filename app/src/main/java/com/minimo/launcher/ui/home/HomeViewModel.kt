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
                    _state.update {
                        _state.value.copy(
                            allApps = appUtils.mapToAppInfo(appInfoList, includeSettings = true)
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

    private val _state = MutableStateFlow(
        HomeScreenState(
            initialLoaded = false,
            favouriteApps = emptyList(),
            allApps = emptyList(),
            isBottomSheetExpanded = false
        )
    )
    val state: StateFlow<HomeScreenState> = _state

    private val _launchApp = Channel<String>(Channel.BUFFERED)
    val launchApp: Flow<String> = _launchApp.receiveAsFlow()

    private val _launchSettings = Channel<Unit>(Channel.BUFFERED)
    val launchSettings: Flow<Unit> = _launchSettings.receiveAsFlow()

    fun setBottomSheetExpanded(isExpanded: Boolean) {
        _state.update {
            _state.value.copy(
                isBottomSheetExpanded = isExpanded
            )
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
            if (info.isLauncherSettings) {
                _launchSettings.send(Unit)
            } else {
                _launchApp.send(info.packageName)
            }
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
}