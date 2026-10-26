package com.fathirverse.theme.data.datasources.local

import android.content.Context
import androidx.core.content.edit
import com.fathirverse.theme.data.exception.ThemeException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ThemeLocalDataSource @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    fun getThemeMode(): String? {
        return try {
            preferences.getString(KEY_THEME_MODE, null)
        } catch (exception: Exception) {
            throw ThemeException(exception)
        }
    }

    fun saveThemeMode(name: String) {
        try {
            preferences.edit {
                putString(KEY_THEME_MODE, name)
            }
        } catch (exception: Exception) {
            throw ThemeException(exception)
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "theme_preferences"
        const val KEY_THEME_MODE = "theme_mode"
    }
}
