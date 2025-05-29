package com.minimo.launcher.ui.settings.customisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.PreferenceHelper
import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.HomeAppsAlignment
import com.minimo.launcher.utils.HomeClockAlignment
import com.minimo.launcher.utils.HomeClockMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomisationViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {
    private val _state = MutableStateFlow(CustomisationState())
    val state: StateFlow<CustomisationState> = _state

    init {
        viewModelScope.launch {
            preferenceHelper.getThemeMode().collect { mode ->
                _state.update {
                    it.copy(themeMode = mode)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeAppsAlignment().collect { alignment ->
                _state.update {
                    it.copy(homeAppsAlignment = alignment)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeClockAlignment().collect { alignment ->
                _state.update {
                    it.copy(homeClockAlignment = alignment)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getShowHomeClock().collect { show ->
                _state.update {
                    it.copy(showHomeClock = show)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getShowStatusBar().collect { show ->
                _state.update {
                    it.copy(showStatusBar = show)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeTextSizeFlow().collect { size ->
                _state.update {
                    it.copy(homeTextSize = size.toFloat())
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getAutoOpenKeyboardAllApps().collect { open ->
                _state.update {
                    it.copy(autoOpenKeyboardAllApps = open)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getDynamicTheme().collect { enable ->
                _state.update {
                    it.copy(dynamicTheme = enable)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeClockMode().collect { mode ->
                _state.update {
                    it.copy(homeClockMode = mode)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getDoubleTapToLock().collect { enable ->
                _state.update {
                    it.copy(doubleTapToLock = enable)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getTwentyFourHourFormat().collect { enable ->
                _state.update {
                    it.copy(twentyFourHourFormat = enable)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getShowBatteryLevel().collect { enable ->
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
    }

    fun onThemeModeChanged(mode: ThemeMode) {
        viewModelScope.launch {
            preferenceHelper.setThemeMode(mode)
        }
    }

    fun onHomeAppsAlignmentChanged(alignment: HomeAppsAlignment) {
        viewModelScope.launch {
            preferenceHelper.setHomeAppsAlignment(alignment)
        }
    }

    fun onHomeClockAlignmentChanged(alignment: HomeClockAlignment) {
        viewModelScope.launch {
            preferenceHelper.setHomeClockAlignment(alignment)
        }
    }

    fun onHomeClockModeChanged(mode: HomeClockMode) {
        viewModelScope.launch {
            preferenceHelper.setHomeClockMode(mode)
        }
    }

    fun onToggleShowHomeClock() {
        viewModelScope.launch {
            preferenceHelper.setShowHomeClock(_state.value.showHomeClock.not())
        }
    }

    fun onToggleTwentyFourHourFormat() {
        viewModelScope.launch {
            preferenceHelper.setTwentyFourHourFormat(_state.value.twentyFourHourFormat.not())
        }
    }

    fun onToggleShowBatteryLevel() {
        viewModelScope.launch {
            preferenceHelper.setShowBatteryLevel(_state.value.showBatteryLevel.not())
        }
    }

    fun onToggleShowStatusBar() {
        viewModelScope.launch {
            preferenceHelper.setShowStatusBar(_state.value.showStatusBar.not())
        }
    }

    fun onHomeTextSizeChanged(size: Int) {
        viewModelScope.launch {
            preferenceHelper.setHomeTextSize(size)
        }
    }

    fun onToggleAutoOpenKeyboardAllApps() {
        viewModelScope.launch {
            preferenceHelper.setAutoOpenKeyboardAllApps(_state.value.autoOpenKeyboardAllApps.not())
        }
    }

    fun onToggleDynamicTheme() {
        viewModelScope.launch {
            preferenceHelper.setDynamicTheme(_state.value.dynamicTheme.not())
        }
    }

    fun onToggleDoubleTapToLock() {
        viewModelScope.launch {
            preferenceHelper.setDoubleTapToLock(_state.value.doubleTapToLock.not())
        }
    }

    fun onToggleShowHiddenAppsInSearch() {
        viewModelScope.launch {
            preferenceHelper.setShowHiddenAppsInSearch(_state.value.showHiddenAppsInSearch.not())
        }
    }

    fun onToggleDrawerSearchBarAtBottom() {
        viewModelScope.launch {
            preferenceHelper.setDrawerSearchBarAtBottom(_state.value.drawerSearchBarAtBottom.not())
        }
    }

    fun onToggleApplyHomeAppSizeToAllApps() {
        viewModelScope.launch {
            preferenceHelper.setHomeAppSizeToAllApps(_state.value.applyHomeAppSizeToAllApps.not())
        }
    }

    /*
    * On start of the screen, if the preference flag is enabled and
    * lock screen permission is not active, then set the preference flag to false
    * */
    fun onLockScreenPermissionNotEnableOnStarted() {
        viewModelScope.launch {
            val doubleTapToLock = preferenceHelper.getDoubleTapToLock().firstOrNull() ?: false
            if (doubleTapToLock) {
                preferenceHelper.setDoubleTapToLock(false)
            }
        }
    }
}