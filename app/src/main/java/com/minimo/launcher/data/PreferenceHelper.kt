package com.minimo.launcher.data

import com.minimo.launcher.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface PreferenceHelper {
    suspend fun setIsIntroCompleted(isCompleted: Boolean)
    fun getIsIntroCompletedFlow(): Flow<Boolean>

    suspend fun setThemeMode(mode: ThemeMode)
    fun getThemeMode(): Flow<ThemeMode>
}