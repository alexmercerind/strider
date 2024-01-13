package com.alexmercerind.strider.repository

import android.app.Application
import com.alexmercerind.strider.db.StepDatabase
import com.alexmercerind.strider.model.Step
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class StepRepository(private val application: Application) {
    suspend fun insert(value: Step) = withContext(Dispatchers.IO) {
        StepDatabase(application).stepDao().insert(value)
    }

    suspend fun getStepsInRange(from: Instant, to: Instant) = withContext(Dispatchers.IO) {
        StepDatabase(application).stepDao().getStepsInRange(from, to)
    }

    fun watchStepsInRange(from: Instant, to: Instant) =
        StepDatabase(application).stepDao().watchStepsInRange(from, to)

    suspend fun getStepCountInRange(from: Instant, to: Instant) = withContext(Dispatchers.IO) {
        StepDatabase(application).stepDao().getStepCountInRange(from, to)
    }

    fun watchStepCountInRange(from: Instant, to: Instant) =
        StepDatabase(application).stepDao().watchStepCountInRange(from, to)
}
