package com.minimo.launcher.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    init {
        viewModelScope.launch {
            preferenceHelper.getThemeMode().collect { mode ->
                _state.update {
                    _state.value.copy(themeMode = mode)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getShowStatusBar().collect { visible ->
                _state.update {
                    _state.value.copy(statusBarVisible = visible)
                }
            }
        }

        viewModelScope.launch {
            preferenceHelper.getDynamicTheme().collect { enable ->
                _state.update {
                    _state.value.copy(useDynamicTheme = enable)
                }
            }
        }
    }
}