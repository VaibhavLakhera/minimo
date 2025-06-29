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
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val _launchApp = Channel<AppInfo>(Channel.BUFFERED)
    val launchApp: Flow<AppInfo> = _launchApp.receiveAsFlow()

    init {
        viewModelScope.launch {
            updateAllAppsUseCase.invoke()
        }

        viewModelScope.launch {
            appInfoDao.getAllAppsFlow(userHandle = appUtils.getCurrentUserHandle())
                .collect { appInfoList ->
                    val allApps = appUtils.mapToAppInfo(appInfoList)
                    _state.update {
                        it.copy(
                            allApps = allApps,
                            filteredAllApps = getAppsWithSearch(
                                searchText = it.searchText,
                                apps = allApps,
                                includeHiddenApps = it.showHiddenAppsInSearch
                            )
                        )
                    }
                }
        }

        viewModelScope.launch {
            appInfoDao.getFavouriteAppsFlow(userHandle = appUtils.getCurrentUserHandle())
                .collect { appInfoList ->
                    _state.update {
                        it.copy(
                            initialLoaded = true,
                            favouriteApps = appUtils.mapToAppInfo(appInfoList)
                        )
                    }
                }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeAppsAlignment()
                .distinctUntilChanged()
                .collect { alignment ->
                val textAlign = when (alignment) {
                    HomeAppsAlignment.Start -> TextAlign.Start
                    HomeAppsAlignment.Center -> TextAlign.Center
                    HomeAppsAlignment.End -> TextAlign.End
                }
                _state.update {
                    it.copy(appsTextAlign = textAlign)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeClockAlignment()
                .distinctUntilChanged()
                .collect { alignment ->
                val horizontalAlignment = when (alignment) {
                    HomeClockAlignment.Start -> Alignment.Start
                    HomeClockAlignment.Center -> Alignment.CenterHorizontally
                    HomeClockAlignment.End -> Alignment.End
                }
                _state.update {
                    it.copy(homeClockAlignment = horizontalAlignment)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getShowHomeClock()
                .distinctUntilChanged()
                .collect { show ->
                _state.update {
                    it.copy(showHomeClock = show)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeTextSizeFlow()
                .distinctUntilChanged()
                .collect { size ->
                _state.update {
                    it.copy(homeTextSize = size)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getAutoOpenKeyboardAllApps()
                .distinctUntilChanged()
                .collect { open ->
                _state.update {
                    it.copy(autoOpenKeyboardAllApps = open)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeClockMode()
                .distinctUntilChanged()
                .collect { mode ->
                _state.update {
                    it.copy(homeClockMode = mode)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getDoubleTapToLock()
                .distinctUntilChanged()
                .collect { enable ->
                _state.update {
                    it.copy(doubleTapToLock = enable)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getTwentyFourHourFormat()
                .distinctUntilChanged()
                .collect { enable ->
                _state.update {
                    it.copy(twentyFourHourFormat = enable)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getShowBatteryLevel()
                .distinctUntilChanged()
                .collect { enable ->
                _state.update {
                    it.copy(showBatteryLevel = enable)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getShowHiddenAppsInSearch()
                .distinctUntilChanged()
                .collect { enable ->
                    _state.update {
                        it.copy(showHiddenAppsInSearch = enable)
                    }
                }
        }

        viewModelScope.launch {
            preferenceHelper.getDrawerSearchBarAtBottom()
                .distinctUntilChanged()
                .collect { enable ->
                    _state.update {
                        it.copy(drawerSearchBarAtBottom = enable)
                    }
                }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeAppSizeToAllApps()
                .distinctUntilChanged()
                .collect { enable ->
                    _state.update {
                        it.copy(applyHomeAppSizeToAllApps = enable)
                    }
                }
        }

        viewModelScope.launch {
            preferenceHelper.getAutoOpenApp()
                .distinctUntilChanged()
                .collect { enable ->
                    _state.update {
                        it.copy(autoOpenApp = enable)
                    }
                }
        }

        viewModelScope.launch {
            preferenceHelper.getHideAppDrawerArrow()
                .distinctUntilChanged()
                .collect { enable ->
                    _state.update {
                        it.copy(hideAppDrawerArrow = enable)
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
            it.copy(
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
                appInfoDao.removeAppFromFavourite(app.className, app.packageName)
            } else {
                appInfoDao.addAppToFavourite(app.className, app.packageName)
            }
        }
    }

    fun onLaunchAppClick(app: AppInfo) {
        viewModelScope.launch {
            _launchApp.send(app)
        }
    }

    fun onToggleHideClick(app: AppInfo) {
        viewModelScope.launch {
            if (app.isHidden) {
                appInfoDao.removeAppFromHidden(app.className, app.packageName)
            } else {
                appInfoDao.addAppToHidden(app.className, app.packageName)
            }
        }
    }

    fun onRenameAppClick(app: AppInfo) {
        _state.update {
            it.copy(
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
            appInfoDao.renameApp(app.className, app.packageName, name)
        }
    }

    fun onDismissRenameAppDialog() {
        _state.update {
            it.copy(
                renameAppDialog = null
            )
        }
    }

    fun onSearchTextChange(searchText: String) {
        val filteredAllApps = getAppsWithSearch(
            searchText = searchText,
            apps = _state.value.allApps,
            includeHiddenApps = _state.value.showHiddenAppsInSearch
        )
        _state.update {
            it.copy(
                searchText = searchText,
                filteredAllApps = filteredAllApps,
            )
        }
        if (_state.value.autoOpenApp && filteredAllApps.size == 1) {
            _launchApp.trySend(filteredAllApps[0])
        }
    }

    /**
     * If searchText is blank, then it should always exclude the favourite and hidden apps from the list.
     *
     * If searchText is not blank, then it should use the "showHiddenApps" flag to decide whether or
     * not to include the hidden apps in the result.
     * */
    private fun getAppsWithSearch(
        searchText: String,
        apps: List<AppInfo>,
        includeHiddenApps: Boolean
    ): List<AppInfo> {
        if (searchText.isBlank()) {
            return apps.filterNot { appInfo ->
                appInfo.isFavourite || appInfo.isHidden
            }
        }

        if (includeHiddenApps) {
            return apps.filter { appInfo ->
                appInfo.name.contains(searchText, ignoreCase = true)
            }
        }

        return apps.filter { appInfo ->
            !appInfo.isHidden && appInfo.name.contains(searchText, ignoreCase = true)
        }
    }
}