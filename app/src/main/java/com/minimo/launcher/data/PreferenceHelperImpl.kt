package com.minimo.launcher.data

import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.minimo.launcher.ui.theme.ThemeMode
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

    override suspend fun setHomeAppsAlign(align: TextAlign) {
        preferences.edit {
            it[KEY_HOME_APPS_ALIGN] = align.toString()
        }
    }

    override fun getHomeAppsAlign(): Flow<TextAlign> {
        return preferences.data.map {
            val align = it[KEY_HOME_APPS_ALIGN]
            if (align.isNullOrBlank()) {
                TextAlign.Start
            } else {
                val matched = TextAlign.values().find { entry -> entry.toString() == align }
                matched ?: TextAlign.Start
            }
        }
    }
}