package com.minimo.launcher.data

import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.HomeAppsAlignment
import com.minimo.launcher.utils.HomeClockAlignment
import com.minimo.launcher.utils.HomeClockMode
import kotlinx.coroutines.flow.Flow

interface PreferenceHelper {
    suspend fun setIsIntroCompleted(isCompleted: Boolean)
    fun getIsIntroCompletedFlow(): Flow<Boolean>

    suspend fun setThemeMode(mode: ThemeMode)
    fun getThemeMode(): Flow<ThemeMode>

    suspend fun setHomeAppsAlignment(alignment: HomeAppsAlignment)
    fun getHomeAppsAlignment(): Flow<HomeAppsAlignment>

    suspend fun setHomeClockAlignment(alignment: HomeClockAlignment)
    fun getHomeClockAlignment(): Flow<HomeClockAlignment>

    suspend fun setShowHomeClock(show: Boolean)
    fun getShowHomeClock(): Flow<Boolean>

    suspend fun setShowStatusBar(show: Boolean)
    fun getShowStatusBar(): Flow<Boolean>

    suspend fun setHomeTextSize(size: Int)
    fun getHomeTextSizeFlow(): Flow<Int>

    suspend fun setAutoOpenKeyboardAllApps(open: Boolean)
    fun getAutoOpenKeyboardAllApps(): Flow<Boolean>

    suspend fun setDynamicTheme(enable: Boolean)
    fun getDynamicTheme(): Flow<Boolean>

    suspend fun setHomeClockMode(mode: HomeClockMode)
    fun getHomeClockMode(): Flow<HomeClockMode>

    suspend fun setDoubleTapToLock(enable: Boolean)
    fun getDoubleTapToLock(): Flow<Boolean>

    suspend fun setTwentyFourHourFormat(enable: Boolean)
    fun getTwentyFourHourFormat(): Flow<Boolean>
}