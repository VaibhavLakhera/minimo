package com.minimo.launcher.ui.hidden_apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HiddenAppsViewModel @Inject constructor(
    private val appInfoDao: AppInfoDao,
    private val appUtils: AppUtils
) : ViewModel() {
    private val _state =
        MutableStateFlow(HiddenAppsState(initialLoaded = false, hiddenApps = emptyList()))
    val state: StateFlow<HiddenAppsState> = _state

    init {
        viewModelScope.launch {
            appInfoDao.getHiddenAppsFlow()
                .collect { appInfoList ->
                    _state.update {
                        state.value.copy(
                            initialLoaded = true,
                            hiddenApps = appUtils.mapToAppInfo(appInfoList)
                        )
                    }
                }
        }
    }

    fun onRemoveAppFromHiddenClicked(packageName: String) {
        viewModelScope.launch {
            appInfoDao.removeAppFromHidden(packageName)
        }
    }
}