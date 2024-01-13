package com.alexmercerind.strider.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alexmercerind.strider.model.Step
import com.alexmercerind.strider.repository.StepRepository
import java.time.Instant

class StepViewModel(private val application: Application) : AndroidViewModel(application) {
    private val repository = StepRepository(application)

    suspend fun insert(value: Step) = repository.insert(value)

    suspend fun getStepsInRange(from: Instant, to: Instant) = repository.getStepsInRange(from, to)

    fun watchStepsInRange(from: Instant, to: Instant) = repository.watchStepsInRange(from, to)

    suspend fun getStepCountInRange(from: Instant, to: Instant) =
        repository.getStepCountInRange(from, to)

    fun watchStepCountInRange(from: Instant, to: Instant) =
        repository.watchStepCountInRange(from, to)
}