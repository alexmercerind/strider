package com.alexmercerind.strider.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alexmercerind.strider.enums.Theme
import com.alexmercerind.strider.repository.SettingsRepository

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    val goal get() = repository.goal
    val theme get() = repository.theme

    fun saveGoal(value: Long) = repository.saveGoal(value)

    fun saveTheme(value: Theme) = repository.saveTheme(value)

}
