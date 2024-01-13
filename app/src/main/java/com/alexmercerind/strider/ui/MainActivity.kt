package com.alexmercerind.strider.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexmercerind.strider.enums.Theme
import com.alexmercerind.strider.ui.navigation.Destinations
import com.alexmercerind.strider.ui.theme.StriderTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            val stepViewModel = viewModel<StepViewModel>()
            val settingsViewModel = viewModel<SettingsViewModel>()
            val userDetailsViewModel = viewModel<UserDetailsViewModel>()

            var physicalActivityPermissionState: PermissionState? = null
            var notificationsPermissionState: PermissionState? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !LocalInspectionMode.current) {
                physicalActivityPermissionState = rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !LocalInspectionMode.current) {
                notificationsPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
            }

            // --------------------------------------------------
            val hasPermission by remember {
                derivedStateOf {
                    physicalActivityPermissionState?.status?.isGranted ?: false && notificationsPermissionState?.status?.isGranted ?: false
                }
            }
            // --------------------------------------------------
            val hasUserDetails = userDetailsViewModel.hasUserDetails
            // --------------------------------------------------


            // --------------------------------------------------
            val startDestination = when {
                !hasPermission -> Destinations.Companion.PermissionsScreen.route
                !hasUserDetails -> Destinations.Companion.UserDetailsScreen.route
                else -> Destinations.Companion.HomeScreen.route
            }
            // --------------------------------------------------


            val system = isSystemInDarkTheme()
            val theme by settingsViewModel.theme.collectAsState()
            val darkTheme by remember {
                derivedStateOf {
                    when(theme) {
                        Theme.SYSTEM -> system
                        Theme.LIGHT -> false
                        Theme.DARK -> true
                    }
                }
            }

            StriderTheme(
                darkTheme = darkTheme
            ) {
                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    startDestination = startDestination,
                ) {
                    composable(Destinations.Companion.HomeScreen.route) {
                        HomeScreen(
                            navController = navController,
                            getStepsInRange = stepViewModel::getStepsInRange,
                            watchStepsInRange = stepViewModel::watchStepsInRange,
                            getStepCountInRange = stepViewModel::getStepCountInRange,
                            watchStepCountInRange = stepViewModel::watchStepCountInRange
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
                    composable(Destinations.Companion.SettingsScreen.route) {
                        SettingsScreen(
                            navController = navController,
                            goal = settingsViewModel.goal,
                            theme = settingsViewModel.theme,
                            onGoalSave = settingsViewModel::saveGoal,
                            onThemeSave = settingsViewModel::saveTheme
                        )
                    }
                }
            }
        }
    }
}
