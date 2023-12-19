package com.alexmercerind.strider.ui

import android.Manifest
import android.app.Service
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexmercerind.strider.ui.navigation.Destinations
import com.alexmercerind.strider.ui.theme.StriderTheme
import com.alexmercerind.strider.utils.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.ZonedDateTime

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = getSystemService(Service.SENSOR_SERVICE) as SensorManager
        val stepCounterSensor = service.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepDetectorSensor = service.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        val current = mutableSetOf<ZonedDateTime>()

        var stepCounter = 0
        var stepDetector = 0

        var v0 = -1

        Log.d(Constants.LOG_TAG, stepCounterSensor.toString())
        Log.d(Constants.LOG_TAG, stepDetectorSensor.toString())
        service.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val v1 = event?.values?.toList()?.firstOrNull()?.toInt()
                if (v1 != null) {
                    if (v0 > 0) {
                        stepCounter = v1 - v0
                        Log.d(Constants.LOG_TAG, "TYPE_STEP_COUNTER: $stepCounter")
                        Log.d(Constants.LOG_TAG, "TYPE_STEP_DETECTOR: $stepDetector")
                    }
                    v0 = v1
                    stepDetector = 0
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST)
        service.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                stepDetector++
                current.add(ZonedDateTime.now())
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST)


        enableEdgeToEdge()
        setContent {
//            var state by remember { mutableStateOf("") }
//
//            LaunchedEffect("") {
//                Timer().scheduleAtFixedRate(object : TimerTask() {
//                    override fun run() {
//                        current.removeIf {
//                            it.isBefore(ZonedDateTime.now().minusSeconds(5L))
//                        }
//                        state = when {
//                            (current.size == 0) -> "idle"
//                            (current.size <= 79.0F / 60.0F * 5.0F) -> "slow"
//                            (current.size <= 99.0F / 60.0F * 5.0F) -> "medium"
//                            (current.size <= 119.0F / 60.0F * 5.0F) -> "brisk"
//                            else -> "fast"
//                        }
//                    }
//
//                }, 0, 1000)
//            }
//            Scaffold { padding ->
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(text = state, style = MaterialTheme.typography.headlineLarge)
//                }
//            }

            val navController = rememberNavController()

            val userDetailsViewModel = viewModel<UserDetailsViewModel>()

            var physicalActivityPermissionState: PermissionState? = null
            var notificationsPermissionState: PermissionState? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !LocalInspectionMode.current) {
                physicalActivityPermissionState =
                    rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !LocalInspectionMode.current) {
                notificationsPermissionState =
                    rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
            }

            val hasPermission by remember {
                derivedStateOf {
                    (physicalActivityPermissionState?.status?.isGranted ?: false) &&
                            (notificationsPermissionState?.status?.isGranted ?: false)
                }
            }
            val hasUserDetails = userDetailsViewModel.hasUserDetails

            val startDestination = when {
                !hasPermission -> Destinations.Companion.PermissionsScreen.route
                !hasUserDetails -> Destinations.Companion.UserDetailsScreen.route
                else -> Destinations.Companion.HomeScreen.route
            }

            StriderTheme {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        fadeIn(animationSpec = tween(300, easing = LinearEasing)) + slideIntoContainer(
                            animationSpec = tween(300, easing = EaseIn),
                            towards = AnimatedContentTransitionScope.SlideDirection.Start
                        )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300, easing = LinearEasing)) + slideOutOfContainer(
                            animationSpec = tween(300, easing = EaseOut),
                            towards = AnimatedContentTransitionScope.SlideDirection.End
                        )
                    }
                ) {
                    composable(Destinations.Companion.HomeScreen.route) {
                        HomeScreen(
                            navController = navController
                        )
                    }
                    composable(Destinations.Companion.PermissionsScreen.route) {
                        PermissionsScreen(
                            navController = navController
                        )
                    }
                    composable(Destinations.Companion.UserDetailsScreen.route) {
                        val nameValue = remember { userDetailsViewModel.name.value }
                        val genderValue = remember { userDetailsViewModel.gender.value }
                        val heightValue = remember { userDetailsViewModel.height.value }
                        val weightValue = remember { userDetailsViewModel.weight.value }
                        UserDetailsScreen(
                            navController = navController,
                            nameValue = nameValue,
                            genderValue = genderValue,
                            heightValue = heightValue,
                            weightValue = weightValue,
                            onSave = userDetailsViewModel::save
                        )
                    }
                }
            }
        }
    }
}
