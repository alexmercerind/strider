package com.alexmercerind.strider.repository

import android.app.Application
import android.content.Context
import com.alexmercerind.strider.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class UserDetailsRepository(application: Application) {
    val name: StateFlow<String>
        get() = _name
    private val _name = MutableStateFlow("")

    val gender: StateFlow<String>
        get() = _gender
    private val _gender = MutableStateFlow("")

    val height: StateFlow<Float>
        get() = _height
    private val _height = MutableStateFlow(0.0F)

    val weight: StateFlow<Float>
        get() = _weight
    private val _weight = MutableStateFlow(0.0F)

    val hasUserDetails: Boolean
        get() = validate(name.value, gender.value, height.value, weight.value)

    private val sharedPreferences = application.getSharedPreferences(
        application.getString(R.string.app_name),
        Context.MODE_PRIVATE
    )

    fun save(name: String, gender: String, height: Float, weight: Float): Boolean {
        if (validate(name, gender, height, weight)) {
            GlobalScope.launch(Dispatchers.IO) {
                _name.emit(name)
                _gender.emit(gender)
                _height.emit(height)
                _weight.emit(weight)
                with(sharedPreferences.edit()) {
                    putString(SHARED_PREFERENCES_KEY_NAME, name)
                    putString(SHARED_PREFERENCES_KEY_GENDER, gender)
                    putFloat(SHARED_PREFERENCES_KEY_HEIGHT, height)
                    putFloat(SHARED_PREFERENCES_KEY_WEIGHT, weight)
                    apply()
                }
            }
            return true
        }
        return false
    }

    init {
        _name.update { sharedPreferences.getString(SHARED_PREFERENCES_KEY_NAME, "") ?: "" }
        _gender.update { sharedPreferences.getString(SHARED_PREFERENCES_KEY_GENDER, "") ?: "" }
        _height.update { sharedPreferences.getFloat(SHARED_PREFERENCES_KEY_HEIGHT, 0.0F) }
        _weight.update { sharedPreferences.getFloat(SHARED_PREFERENCES_KEY_WEIGHT, 0.0F) }
    }

    companion object {
        const val SHARED_PREFERENCES_KEY_NAME = "UserDetailsRepository/NAME"
        const val SHARED_PREFERENCES_KEY_GENDER = "UserDetailsRepository/GENDER"
        const val SHARED_PREFERENCES_KEY_HEIGHT = "UserDetailsRepository/HEIGHT"
        const val SHARED_PREFERENCES_KEY_WEIGHT = "UserDetailsRepository/WEIGHT"

        fun validate(name: String, gender: String, height: Float, weight: Float) =
            name.isNotBlank() && gender.isNotBlank() && height > 0.0F && weight > 0.0F

        @Volatile
        private var instance: UserDetailsRepository? = null

        private val lock = Any()

        operator fun invoke(application: Application) = instance ?: synchronized(lock) {
            instance ?: UserDetailsRepository(application).also { instance = it }
        }
    }
}
