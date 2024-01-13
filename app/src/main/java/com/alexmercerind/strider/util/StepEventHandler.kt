package com.alexmercerind.strider.util

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.alexmercerind.strider.enum.Gender
import com.alexmercerind.strider.enum.WalkSpeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.math.pow

/**
 * StepEventHandler handles a new step event:
 *
 * * Walk speed approximation.
 * * Metabolic Equivalent (MET) calculation.
 * * Insertion into Room (SQLite) database.
 */
class StepEventHandler(
    private val gender: Gender,
    private val lifecycleScope: LifecycleCoroutineScope
) {
    private val current = mutableSetOf<Instant>()

    private var refractoryPeriodJob: Job? = null

    private val _walkSpeed = MutableStateFlow(WalkSpeed.STILL)
    val walkSpeed
        get() = _walkSpeed.asStateFlow()

    private val _MET = MutableStateFlow(0.0F)
    val MET
        get() = _MET.asStateFlow()

    // Notify about a new step event.
    fun event() {
        lifecycleScope.launch {
            val timestamp = Instant.now()

            current.removeIf {
                it.isBefore(Instant.now().minusSeconds(5L))
            }
            current.add(timestamp)

            // https://www.maine.gov/mdot/challengeme/topics/docs/2019/may/How-to-Walk-with-Proper-Form-and-Technique-for-Fitness.pdf
            val walkSpeedValue = when {
                current.size.toFloat() < (WALK_SPEED_MEDIUM_THRESHOLD / 60.0F * 5.0F) -> WalkSpeed.SLOW
                current.size.toFloat() < (WALK_SPEED_FAST_THRESHOLD / 60.0F * 5.0F) -> WalkSpeed.MEDIUM
                else -> WalkSpeed.FAST
            }
            _walkSpeed.emit(walkSpeedValue)

            // https://www.researchgate.net/publication/49813492_Determination_of_step_rate_thresholds_corresponding_to_physical_activity_classifications_in_adults
            val METValue = when(gender) {
                Gender.MALE, Gender.UNSPECIFIED -> getMaleMETFromStepRate(current.size * 60.0F)
                Gender.FEMALE -> getFemaleMETFromStepRate(current.size * 60.0F)
            }.toFloat()
            _MET.emit(METValue)

            Log.d(Constants.LOG_TAG, "walkSpeedValue=$walkSpeedValue")
            Log.d(Constants.LOG_TAG, "METValue=$METValue")

            refractoryPeriodJob?.cancel()
            refractoryPeriodJob = lifecycleScope.launch(Dispatchers.IO) {
                delay(2000L)
                if (isActive) {
                    _walkSpeed.emit(WalkSpeed.STILL)
                }
            }
        }
    }

    private fun getMaleMETFromStepRate(value: Float) = 0.00004325 * (value / 5.0).pow(2.4528)
    private fun getFemaleMETFromStepRate(value: Float) = 0.00004325 * (value / 5.0).pow(2.4528)

    companion object {
        const val WALK_SPEED_MEDIUM_THRESHOLD = 135
        const val WALK_SPEED_FAST_THRESHOLD = 160
    }
}
