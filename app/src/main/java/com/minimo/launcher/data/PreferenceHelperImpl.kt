package com.minimo.launcher.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.minimo.launcher.ui.theme.ThemeMode
import com.minimo.launcher.utils.Constants
import com.minimo.launcher.utils.HomeAppsAlignment
import com.minimo.launcher.utils.HomeClockAlignment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceHelperImpl @Inject constructor(
    private val preferences: DataStore<Preferences>
) : PreferenceHelper {
    companion object {
        private val KEY_INTRO_COMPLETED = booleanPreferencesKey("KEY_INTRO_COMPLETED")
        private val KEY_THEME_MODE = stringPreferencesKey("KEY_THEME_MODE")
        private val KEY_HOME_APPS_ALIGN = stringPreferencesKey("KEY_HOME_APPS_ALIGN")
        private val KEY_HOME_CLOCK_ALIGNMENT = stringPreferencesKey("KEY_HOME_CLOCK_ALIGNMENT")
        private val KEY_SHOW_HOME_CLOCK = booleanPreferencesKey("KEY_SHOW_HOME_CLOCK")
        private val KEY_SHOW_STATUS_BAR = booleanPreferencesKey("KEY_SHOW_STATUS_BAR")
        private val KEY_HOME_TEXT_SIZE = intPreferencesKey("KEY_HOME_TEXT_SIZE")
        private val KEY_AUTO_OPEN_KEYBOARD_ALL_APPS =
            booleanPreferencesKey("KEY_AUTO_OPEN_KEYBOARD_ALL_APPS")
    }

    override suspend fun setIsIntroCompleted(isCompleted: Boolean) {
        preferences.edit {
            it[KEY_INTRO_COMPLETED] = isCompleted
        }
    }

    override fun getIsIntroCompletedFlow(): Flow<Boolean> {
        return preferences.data.map { it[KEY_INTRO_COMPLETED] ?: false }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        preferences.edit {
            it[KEY_THEME_MODE] = mode.name
        }
    }

    override fun getThemeMode(): Flow<ThemeMode> {
        return preferences.data.map {
            val mode = it[KEY_THEME_MODE]
            if (!mode.isNullOrBlank()
                && ThemeMode.entries.any { entry -> entry.name == mode }
            ) {
                ThemeMode.valueOf(mode)
            } else {
                ThemeMode.System
            }
        }
    }

    override suspend fun setHomeAppsAlignment(alignment: HomeAppsAlignment) {
        preferences.edit {
            it[KEY_HOME_APPS_ALIGN] = alignment.name
        }
    }

    override fun getHomeAppsAlignment(): Flow<HomeAppsAlignment> {
        return preferences.data.map {
            val alignment = it[KEY_HOME_APPS_ALIGN]
            if (!alignment.isNullOrBlank()
                && HomeAppsAlignment.entries.any { entry -> entry.name == alignment }
            ) {
                HomeAppsAlignment.valueOf(alignment)
            } else {
                HomeAppsAlignment.Start
            }
        }
    }

    override suspend fun setHomeClockAlignment(alignment: HomeClockAlignment) {
        preferences.edit {
            it[KEY_HOME_CLOCK_ALIGNMENT] = alignment.name
        }
    }

    override fun getHomeClockAlignment(): Flow<HomeClockAlignment> {
        return preferences.data.map {
            val alignment = it[KEY_HOME_CLOCK_ALIGNMENT]
            if (!alignment.isNullOrBlank()
                && HomeClockAlignment.entries.any { entry -> entry.name == alignment }
            ) {
                HomeClockAlignment.valueOf(alignment)
            } else {
                HomeClockAlignment.Start
            }
        }
    }

    override suspend fun setShowHomeClock(show: Boolean) {
        preferences.edit {
            it[KEY_SHOW_HOME_CLOCK] = show
        }
    }

    override fun getShowHomeClock(): Flow<Boolean> {
        return preferences.data.map { it[KEY_SHOW_HOME_CLOCK] ?: false }
    }

    override suspend fun setShowStatusBar(show: Boolean) {
        preferences.edit {
            it[KEY_SHOW_STATUS_BAR] = show
        }
    }

    override fun getShowStatusBar(): Flow<Boolean> {
        return preferences.data.map { it[KEY_SHOW_STATUS_BAR] ?: true }
    }

    override suspend fun setHomeTextSize(size: Int) {
        preferences.edit { preferences ->
            preferences[KEY_HOME_TEXT_SIZE] = size
        }
    }

    override fun getHomeTextSizeFlow(): Flow<Int> {
        return preferences.data.map {
            it[KEY_HOME_TEXT_SIZE] ?: Constants.DEFAULT_HOME_TEXT_SIZE
        }
    }

    override suspend fun setAutoOpenKeyboardAllApps(open: Boolean) {
        preferences.edit {
            it[KEY_AUTO_OPEN_KEYBOARD_ALL_APPS] = open
        }
    }

    override fun getAutoOpenKeyboardAllApps(): Flow<Boolean> {
        return preferences.data.map { it[KEY_AUTO_OPEN_KEYBOARD_ALL_APPS] ?: false }
    }
}