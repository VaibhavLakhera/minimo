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
import kotlinx.coroutines.flow.map
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
                    _allAppsFlow.value = appUtils.mapToAppInfo(appInfoList, includeSettings = true)
                }
        }
    }

    private val _isBottomSheetExpanded = MutableStateFlow(false)
    val isBottomSheetExpanded: StateFlow<Boolean> = _isBottomSheetExpanded

    private val _renameAppDialog = MutableStateFlow<AppInfo?>(null)
    val renameAppDialog: StateFlow<AppInfo?> = _renameAppDialog

    private val _launchApp = Channel<String>(Channel.BUFFERED)
    val launchApp: Flow<String> = _launchApp.receiveAsFlow()

    private val _launchSettings = Channel<Unit>(Channel.BUFFERED)
    val launchSettings: Flow<Unit> = _launchSettings.receiveAsFlow()

    val favouriteAppsFlow = appInfoDao.getFavouriteAppsFlow().map {
        appUtils.mapToAppInfo(it)
    }

    private val _allAppsFlow = MutableStateFlow<List<AppInfo>>(emptyList())
    val allAppsFlow: StateFlow<List<AppInfo>> = _allAppsFlow

    fun setBottomSheetExpanded(isExpanded: Boolean) {
        _isBottomSheetExpanded.update { isExpanded }
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
        _renameAppDialog.update { app }
    }

    fun onRenameApp(newName: String) {
        val app = _renameAppDialog.value ?: return
        onDismissRenameAppDialog()
        viewModelScope.launch {
            val name = newName.ifBlank {
                app.appName
            }
            appInfoDao.renameApp(app.packageName, name)
        }
    }

    fun onDismissRenameAppDialog() {
        _renameAppDialog.update { null }
    }
}