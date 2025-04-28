package com.minimo.launcher.ui.home

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.data.PreferenceHelper
import com.minimo.launcher.data.usecase.UpdateAllAppsUseCase
import com.minimo.launcher.ui.entities.AppInfo
import com.minimo.launcher.utils.AppUtils
import com.minimo.launcher.utils.HomeAppsAlignment
import com.minimo.launcher.utils.HomeClockAlignment
import com.minimo.launcher.utils.HomePressedNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val updateAllAppsUseCase: UpdateAllAppsUseCase,
    private val appInfoDao: AppInfoDao,
    private val appUtils: AppUtils,
    private val preferenceHelper: PreferenceHelper,
    private val homePressedNotifier: HomePressedNotifier
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state

    private val _triggerHomePressed = MutableStateFlow(false)
    val triggerHomePressed: StateFlow<Boolean> = _triggerHomePressed.asStateFlow()

    private val _launchApp = Channel<String>(Channel.BUFFERED)
    val launchApp: Flow<String> = _launchApp.receiveAsFlow()

    init {
        viewModelScope.launch {
            updateAllAppsUseCase.invoke()
        }

        viewModelScope.launch {
            appInfoDao.getAllAppsFlow()
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

        viewModelScope.launch {
            preferenceHelper.getHomeAppsAlignment().collect { alignment ->
                val textAlign = when (alignment) {
                    HomeAppsAlignment.Start -> TextAlign.Start
                    HomeAppsAlignment.Center -> TextAlign.Center
                    HomeAppsAlignment.End -> TextAlign.End
                }
                _state.update {
                    _state.value.copy(appsTextAlign = textAlign)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeClockAlignment().collect { alignment ->
                val horizontalAlignment = when (alignment) {
                    HomeClockAlignment.Start -> Alignment.Start
                    HomeClockAlignment.Center -> Alignment.CenterHorizontally
                    HomeClockAlignment.End -> Alignment.End
                }
                _state.update {
                    _state.value.copy(homeClockAlignment = horizontalAlignment)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getShowHomeClock().collect { show ->
                _state.update {
                    _state.value.copy(showHomeClock = show)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeTextSizeFlow().collect { size ->
                _state.update {
                    _state.value.copy(homeTextSize = size)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getAutoOpenKeyboardAllApps().collect { open ->
                _state.update {
                    _state.value.copy(autoOpenKeyboardAllApps = open)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeClockMode().collect { mode ->
                _state.update {
                    _state.value.copy(homeClockMode = mode)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getDoubleTapToLock().collect { enable ->
                _state.update {
                    _state.value.copy(doubleTapToLock = enable)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getTwentyFourHourFormat().collect { enable ->
                _state.update {
                    _state.value.copy(twentyFourHourFormat = enable)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getShowBatteryLevel().collect { enable ->
                _state.update {
                    _state.value.copy(showBatteryLevel = enable)
                }
            }
        }

        listenForHomePressedEvent()
    }

    private fun listenForHomePressedEvent() {
        homePressedNotifier.homePressedEvent
            .onEach {
                _triggerHomePressed.update { true }

                // Reset the flag after a delay to accept future triggers.
                // Delay is added so that bottom sheet will properly animate back to the closed position.
                viewModelScope.launch {
                    delay(1500)
                    _triggerHomePressed.update { false }
                }
            }
            .launchIn(viewModelScope)
    }

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

    fun onToggleFavouriteAppClick(app: AppInfo) {
        viewModelScope.launch {
            if (app.isFavourite) {
                appInfoDao.removeAppFromFavourite(app.packageName)
            } else {
                appInfoDao.addAppToFavourite(app.packageName)
            }
        }
    }

    fun onLaunchAppClick(app: AppInfo) {
        viewModelScope.launch {
            _launchApp.send(app.packageName)
        }
    }

    fun onToggleHideClick(app: AppInfo) {
        viewModelScope.launch {
            if (app.isHidden) {
                appInfoDao.removeAppFromHidden(app.packageName)
            } else {
                appInfoDao.addAppToHidden(app.packageName)
            }
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
        if (searchText.isBlank()) return apps.filterNot { appInfo ->
            appInfo.isFavourite || appInfo.isHidden
        }

        return apps.filter { appInfo ->
            appInfo.name.contains(searchText, ignoreCase = true)
        }
    }
}