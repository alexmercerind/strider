package com.alexmercerind.strider.ui

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alexmercerind.strider.R
import com.alexmercerind.strider.extensions.calories
import com.alexmercerind.strider.extensions.distance
import com.alexmercerind.strider.extensions.toCaloriesString
import com.alexmercerind.strider.extensions.toDistanceString
import com.alexmercerind.strider.model.Step
import com.alexmercerind.strider.service.StepReaderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.reflect.KSuspendFunction2

@Composable
fun DayAnalytics(
    day: LocalDate,
    goal: StateFlow<Long>,
    getStepsInRange: KSuspendFunction2<Instant, Instant, List<Step>>,
    watchStepsInRange: (Instant, Instant) -> LiveData<List<Step>>,
    getStepCountInRange: KSuspendFunction2<Instant, Instant, Long>,
    watchStepCountInRange: (Instant, Instant) -> LiveData<Long>
) {

    val from = day.atStartOfDay(ZoneId.systemDefault()).toInstant()
    val to = day.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

    val steps by remember(from, to) { watchStepsInRange(from, to) }.observeAsState(listOf())

    val context = LocalContext.current
    LaunchedEffect(rememberCoroutineScope()) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, StepReaderService::class.java).apply {
                action = StepReaderService.ACTION_START
            }
            context.startService(intent)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        val current by remember(from, to) {
            watchStepCountInRange(from, to)
        }.observeAsState(0L)
        val target by goal.collectAsState()

        DayStepCounter(
            current = current,
            target = target
        )

        Spacer(modifier = Modifier.height(16.dp))

        val distance = remember(steps.size) { steps.distance().toDistanceString(context) }
        DetailCard(
            icon = R.drawable.baseline_directions_walk_24,
            headlineText = distance,
            supportingText = stringResource(id = R.string.label_distance)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val calories = remember(steps.size) { steps.calories().toCaloriesString(context) }
        DetailCard(
            icon = R.drawable.baseline_local_fire_department_24,
            headlineText = calories,
            supportingText = stringResource(id = R.string.label_calories)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
@Preview()
fun DayAnalyticsPreview() {
    suspend fun fun1(from: Instant, to: Instant) = listOf<Step>()
    suspend fun fun2(from: Instant, to: Instant) = 0L

    DayAnalytics(
        day = LocalDate.now(),
        goal = MutableStateFlow(10000L),
        getStepsInRange = ::fun1,
        watchStepsInRange = { _, _ -> MutableLiveData() },
        getStepCountInRange = ::fun2,
        watchStepCountInRange = { _, _ -> MutableLiveData() }
    )
}
