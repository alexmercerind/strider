package com.alexmercerind.strider.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alexmercerind.strider.R
import com.alexmercerind.strider.enums.WalkSpeed
import com.alexmercerind.strider.extensions.calories
import com.alexmercerind.strider.extensions.distance
import com.alexmercerind.strider.extensions.toCaloriesString
import com.alexmercerind.strider.extensions.toDistanceString
import com.alexmercerind.strider.model.Step
import com.alexmercerind.strider.service.StepReaderService
import com.alexmercerind.strider.ui.navigation.Destinations
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.reflect.KSuspendFunction2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    getStepsInRange: KSuspendFunction2<Instant, Instant, List<Step>>,
    watchStepsInRange: (Instant, Instant) -> LiveData<List<Step>>,
    getStepCountInRange: KSuspendFunction2<Instant, Instant, Long>,
    watchStepCountInRange: (Instant, Instant) -> LiveData<Long>
) {

    val from = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
    val to = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

    val steps by remember { watchStepsInRange(from, to) }.observeAsState(listOf())

    val context = LocalContext.current
    LaunchedEffect(rememberCoroutineScope()) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, StepReaderService::class.java).apply {
                action = StepReaderService.ACTION_START
            }
            context.startService(intent)
        }
    }

    val state = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = state, snapAnimationSpec = null)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.details_today)) },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { navController.navigate(Destinations.Companion.UserDetailsScreen.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                    IconButton(onClick = { navController.navigate(Destinations.Companion.SettingsScreen.route) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                    contentDescription = stringResource(id = R.string.analytics)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                val current by remember {
                    watchStepCountInRange(from, to)
                }.observeAsState(0L)
                DayStepCounter(current = current, target = 10000L)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {

                val STILL = stringResource(id = R.string.unit_speed_still)
                val SLOW = stringResource(id = R.string.unit_speed_slow)
                val MEDIUM = stringResource(id = R.string.unit_speed_medium)
                val FAST = stringResource(id = R.string.unit_speed_fast)

                var walkSpeed by remember { mutableStateOf(STILL) }

                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == StepReaderService.ACTION_WALK_SPEED) {
                            val value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                intent.getSerializableExtra(
                                    StepReaderService.ACTION_WALK_SPEED_ARG,
                                    WalkSpeed::class.java
                                )
                            } else {
                                intent.getSerializableExtra(StepReaderService.ACTION_WALK_SPEED_ARG) as WalkSpeed
                            }
                            walkSpeed = when (value) {
                                WalkSpeed.STILL -> STILL
                                WalkSpeed.SLOW -> SLOW
                                WalkSpeed.MEDIUM -> MEDIUM
                                WalkSpeed.FAST -> FAST
                                else -> ""
                            }
                        }
                    }
                }


                LaunchedEffect(true, rememberCoroutineScope()) {
                    withContext(Dispatchers.IO) {
                        try {
                            LocalBroadcastManager.getInstance(context).registerReceiver(
                                receiver,
                                IntentFilter(StepReaderService.ACTION_WALK_SPEED)
                            )
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                }

                DisposableEffect(true) {
                    onDispose {
                        try {
                            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                }

                DetailCard(
                    icon = R.drawable.baseline_shutter_speed_24,
                    headlineText = walkSpeed,
                    supportingText = stringResource(id = R.string.label_walking_speed)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                val distance = remember(steps.size) { steps.distance().toDistanceString(context) }
                DetailCard(
                    icon = R.drawable.baseline_directions_walk_24,
                    headlineText = distance,
                    supportingText = stringResource(id = R.string.label_distance)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                val calories = remember(steps.size) { steps.calories().toCaloriesString(context) }
                DetailCard(
                    icon = R.drawable.baseline_local_fire_department_24,
                    headlineText = calories,
                    supportingText = stringResource(id = R.string.label_calories)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun HomeScreenPreview() {
    suspend fun fun1(from: Instant, to: Instant) = listOf<Step>()
    suspend fun fun2(from: Instant, to: Instant) = 0L

    HomeScreen(
        navController = rememberNavController(),
        ::fun1,
        { _, _ -> MutableLiveData() },
        ::fun2,
        { _, _ -> MutableLiveData() }
    )
}
