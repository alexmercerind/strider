package com.alexmercerind.strider.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alexmercerind.strider.repository.UserDetailsRepository
import kotlinx.coroutines.flow.StateFlow

class UserDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserDetailsRepository(application)

    val name: StateFlow<String> get() = repository.name

    val gender: StateFlow<String> get() = repository.gender

    val height: StateFlow<Float> get() = repository.height

    val weight: StateFlow<Float> get() = repository.weight

    suspend fun save(name: String, gender: String, height: Float, weight: Float): Boolean =
        repository.save(name, gender, height, weight)
}
