package com.alexmercerind.strider.util

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.alexmercerind.strider.enums.Gender
import com.alexmercerind.strider.enums.WalkSpeed
import com.alexmercerind.strider.model.Step
import com.alexmercerind.strider.repository.StepRepository
import com.alexmercerind.strider.repository.UserDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * StepEventHandler handles a new step event:
 *
 * * Walk speed approximation.
 * * Metabolic Equivalent (MET) calculation.
 * * Insertion into Room (SQLite) database.
 */
class StepEventHandler(
    private val stepRepository: StepRepository,
    private val userDetailsRepository: UserDetailsRepository,
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
            val instant = Instant.now()

            current.removeIf {
                it.isBefore(instant.minusSeconds(BUFFER_INTERVAL))
            }
            current.add(instant)

            val value = current.size * 60.0F / BUFFER_INTERVAL

            val walkSpeedValue = when {
                value < WALK_SPEED_MEDIUM_THRESHOLD -> WalkSpeed.SLOW
                value < WALK_SPEED_FAST_THRESHOLD -> WalkSpeed.MEDIUM
                else -> WalkSpeed.FAST
            }
            _walkSpeed.emit(walkSpeedValue)

            val METValue = when (userDetailsRepository.gender.value) {
                Gender.MALE, Gender.UNSPECIFIED -> getMaleMETFromStepRate(value)
                Gender.FEMALE -> getFemaleMETFromStepRate(value)
                else -> 0.0F
            }.toFloat()
            _MET.emit(METValue)

            val speedValue = getSpeedFromStepRate(value)

            Log.d(Constants.LOG_TAG, "StepEventHandler/walkSpeedValue=$walkSpeedValue")
            Log.d(Constants.LOG_TAG, "StepEventHandler/METValue=$METValue")
            Log.d(Constants.LOG_TAG, "StepEventHandler/speedValue=$speedValue")

            try {
                stepRepository.insert(
                    Step(
                        instant,
                        METValue,
                        userDetailsRepository.height.value,
                        userDetailsRepository.weight.value,
                        speedValue,
                        walkSpeedValue
                    )
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            refractoryPeriodJob?.cancel()
            refractoryPeriodJob = lifecycleScope.launch(Dispatchers.IO) {
                delay(2000L)
                if (isActive) {
                    _walkSpeed.emit(WalkSpeed.STILL)
                }
            }
        }
    }

    private fun getMaleMETFromStepRate(value: Float) = 0.00004325 * value.pow(2.4528F)
    private fun getFemaleMETFromStepRate(value: Float) = 0.00004325 * value.pow(2.4528F)

    private fun getSpeedFromStepRate(value: Float) =
        userDetailsRepository.height.value * sqrt(2.0F) * value / (100L * 60L)

    companion object {
        const val BUFFER_INTERVAL = 5L
        const val WALK_SPEED_MEDIUM_THRESHOLD = 135L
        const val WALK_SPEED_FAST_THRESHOLD = 160L
    }


    // https://www.maine.gov/mdot/challengeme/topics/docs/2019/may/How-to-Walk-with-Proper-Form-and-Technique-for-Fitness.pdf
    // https://www.researchgate.net/publication/49813492_Determination_of_step_rate_thresholds_corresponding_to_physical_activity_classifications_in_adults
}
