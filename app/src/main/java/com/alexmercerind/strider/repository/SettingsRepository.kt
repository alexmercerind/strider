package com.alexmercerind.strider.repository

import android.app.Application
import android.content.Context
import com.alexmercerind.strider.enums.Theme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class SettingsRepository(application: Application) {
    val goal: StateFlow<Long>
        get() = _goal
    private val _goal = MutableStateFlow(10000L)

    val theme: StateFlow<Theme>
        get() = _theme
    private val _theme = MutableStateFlow(Theme.SYSTEM)

    private val sharedPreferences = application.getSharedPreferences(
        "SettingsRepository",
        Context.MODE_PRIVATE
    )

    fun saveGoal(value: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            _goal.emit(value)
            with(sharedPreferences.edit()) {
                putLong(SHARED_PREFERENCES_KEY_GOAL, value)
                apply()
            }
        }
    }

    fun saveTheme(value: Theme) {
        GlobalScope.launch(Dispatchers.IO) {
            _theme.emit(value)
            with(sharedPreferences.edit()) {
                putInt(SHARED_PREFERENCES_KEY_THEME, value.ordinal)
                apply()
            }
        }
    }

    init {
        _goal.update { sharedPreferences.getLong(SHARED_PREFERENCES_KEY_GOAL, 10000L) }
        _theme.update {Theme.values().getOrElse(sharedPreferences.getInt(SHARED_PREFERENCES_KEY_THEME, 0)) { _ -> Theme.SYSTEM } }
    }

    companion object {
        const val SHARED_PREFERENCES_KEY_GOAL = "SettingsRepository/GOAL"
        const val SHARED_PREFERENCES_KEY_THEME = "SettingsRepository/THEME"

        @Volatile
        private var instance: SettingsRepository? = null

        private val lock = Any()

        operator fun invoke(application: Application) = instance ?: synchronized(lock) {
            instance ?: SettingsRepository(application).also { instance = it }
        }
    }
}
