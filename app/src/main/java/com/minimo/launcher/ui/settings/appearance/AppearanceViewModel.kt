package com.minimo.launcher.ui.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.PreferenceHelper
import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.HomeAppsAlignment
import com.minimo.launcher.utils.HomeClockAlignment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppearanceViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {
    private val _state = MutableStateFlow(AppearanceState())
    val state: StateFlow<AppearanceState> = _state

    init {
        viewModelScope.launch {
            preferenceHelper.getThemeMode().collect { mode ->
                _state.update {
                    _state.value.copy(themeMode = mode)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeAppsAlignment().collect { alignment ->
                _state.update {
                    _state.value.copy(homeAppsAlignment = alignment)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeClockAlignment().collect { alignment ->
                _state.update {
                    _state.value.copy(homeClockAlignment = alignment)
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
            preferenceHelper.getShowStatusBar().collect { show ->
                _state.update {
                    _state.value.copy(showStatusBar = show)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getHomeTextSizeFlow().collect { size ->
                _state.update {
                    _state.value.copy(homeTextSize = size.toFloat())
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

    fun onToggleShowHomeClock() {
        viewModelScope.launch {
            preferenceHelper.setShowHomeClock(_state.value.showHomeClock.not())
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
}