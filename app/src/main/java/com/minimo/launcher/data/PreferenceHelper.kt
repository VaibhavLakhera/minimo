package com.minimo.launcher.data

import androidx.compose.ui.text.style.TextAlign
import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.HomeClockAlignment
import kotlinx.coroutines.flow.Flow

interface PreferenceHelper {
    suspend fun setIsIntroCompleted(isCompleted: Boolean)
    fun getIsIntroCompletedFlow(): Flow<Boolean>

    suspend fun setThemeMode(mode: ThemeMode)
    fun getThemeMode(): Flow<ThemeMode>

    suspend fun setHomeAppsAlign(align: TextAlign)
    fun getHomeAppsAlign(): Flow<TextAlign>

    suspend fun setHomeClockAlignment(alignment: HomeClockAlignment)
    fun getHomeClockAlignment(): Flow<HomeClockAlignment>

    suspend fun setShowHomeClock(show: Boolean)
    fun getShowHomeClock(): Flow<Boolean>
}