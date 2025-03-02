package com.minimo.launcher.ui.settings.appearance

import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.PreferenceHelper
import com.minimo.launcher.ui.theme.ThemeMode
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
            preferenceHelper.getHomeAppsAlign().collect { align ->
                _state.update {
                    _state.value.copy(homeAppsAlign = align)
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
    }

    fun onThemeModeChanged(mode: ThemeMode) {
        viewModelScope.launch {
            preferenceHelper.setThemeMode(mode)
        }
    }

    fun onHomeAppsAlignmentChanged(align: TextAlign) {
        viewModelScope.launch {
            preferenceHelper.setHomeAppsAlign(align)
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
}