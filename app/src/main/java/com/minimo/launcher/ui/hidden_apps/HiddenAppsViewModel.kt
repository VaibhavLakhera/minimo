package com.minimo.launcher.ui.hidden_apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minimo.launcher.data.AppInfoDao
import com.minimo.launcher.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HiddenAppsViewModel @Inject constructor(
    private val appInfoDao: AppInfoDao,
    private val appUtils: AppUtils
) : ViewModel() {
    val hiddenAppsFlow = appInfoDao.getHiddenAppsFlow()
        .map { appUtils.mapToAppInfo(it) }

    fun onRemoveAppFromHiddenClicked(packageName: String) {
        viewModelScope.launch {
            appInfoDao.removeAppFromHidden(packageName)
        }
    }
}