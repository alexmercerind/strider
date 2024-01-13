package com.alexmercerind.strider.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alexmercerind.strider.enum.Gender
import com.alexmercerind.strider.repository.UserDetailsRepository

class UserDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserDetailsRepository(application)

    val name get() = repository.name

    val gender get() = repository.gender

    val height get() = repository.height

    val weight get() = repository.weight

    val hasUserDetails get() = repository.hasUserDetails

    fun save(name: String, gender: Gender?, height: Float, weight: Float): Boolean =
        repository.save(name, gender, height, weight)
}
