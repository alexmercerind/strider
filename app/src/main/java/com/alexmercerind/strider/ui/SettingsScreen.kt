package com.alexmercerind.strider.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alexmercerind.strider.R
import com.alexmercerind.strider.enums.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    goal: StateFlow<Long>,
    theme: StateFlow<Theme>,
    onGoalSave: (Long) -> Unit,
    onThemeSave: (Theme) -> Unit
) {
    val scope = rememberCoroutineScope()

    val goalSheetState = rememberModalBottomSheetState()
    var showGoalBottomSheet by remember { mutableStateOf(false) }

    val themeSheetState = rememberModalBottomSheetState()
    var showThemeBottomSheet by remember { mutableStateOf(false) }

    val state = rememberTopAppBarState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = state, snapAnimationSpec = null)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.settings_general),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                val current by goal.collectAsState()
                ListItem(
                    modifier = Modifier.clickable {
                        showGoalBottomSheet = true
                    },
                    leadingContent = {
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(24.dp),
                            painter = painterResource(R.drawable.baseline_military_tech_24),
                            contentDescription = ""
                        )
                    },
                    headlineContent = { Text(text = stringResource(id = R.string.settings_general_goal)) },
                    supportingContent = { Text(text = current.toString()) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.settings_appearance),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                val current by theme.collectAsState()

                val SYSTEM = stringResource(id = R.string.settings_appearance_theme_system)
                val LIGHT = stringResource(id = R.string.settings_appearance_theme_light)
                val DARK = stringResource(id = R.string.settings_appearance_theme_dark)

                val drawable = when (current) {
                    Theme.SYSTEM -> R.drawable.baseline_brightness_auto_24
                    Theme.LIGHT -> R.drawable.baseline_light_mode_24
                    Theme.DARK -> R.drawable.baseline_dark_mode_24
                }
                val supportingText = when (current) {
                    Theme.SYSTEM -> SYSTEM
                    Theme.LIGHT -> LIGHT
                    Theme.DARK -> DARK
                }

                ListItem(
                    modifier = Modifier.clickable {
                        showThemeBottomSheet = true
                    },
                    leadingContent = {
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(24.dp),
                            painter = painterResource(drawable),
                            contentDescription = ""
                        )
                    },
                    headlineContent = { Text(text = stringResource(id = R.string.settings_appearance_theme)) },
                    supportingContent = { Text(text = supportingText) }
                )

            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showGoalBottomSheet) {
            ModalBottomSheet(
                sheetState = goalSheetState,
                onDismissRequest = {
                    showGoalBottomSheet = false
                }
            ) {
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(true) {
                    focusRequester.requestFocus()
                }

                var value by remember { mutableStateOf(goal.value.toString()) }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .focusRequester(focusRequester),
                    value = value,
                    onValueChange = {
                        value = it
                    },
                    label = { Text(text = stringResource(id = R.string.settings_general_goal)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = {
                        try {
                            val result = value.toLong()
                            if (result > 100L) {
                                onGoalSave(result)

                                scope.launch { goalSheetState.hide() }.invokeOnCompletion {
                                    if (!goalSheetState.isVisible) {
                                        showGoalBottomSheet = false
                                    }
                                }
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }

                    }
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showThemeBottomSheet) {
            ModalBottomSheet(
                sheetState = themeSheetState,
                onDismissRequest = {
                    showThemeBottomSheet = false
                }
            ) {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                onThemeSave(Theme.SYSTEM)

                                scope.launch { themeSheetState.hide() }.invokeOnCompletion {
                                    if (!themeSheetState.isVisible) {
                                        showThemeBottomSheet = false
                                    }
                                }
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_appearance_theme_system))
                    }
                )
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                onThemeSave(Theme.LIGHT)

                                scope.launch { themeSheetState.hide() }.invokeOnCompletion {
                                    if (!themeSheetState.isVisible) {
                                        showThemeBottomSheet = false
                                    }
                                }
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_appearance_theme_light))
                    }
                )
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                onThemeSave(Theme.DARK)

                                scope.launch { themeSheetState.hide() }.invokeOnCompletion {
                                    if (!themeSheetState.isVisible) {
                                        showThemeBottomSheet = false
                                    }
                                }
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_appearance_theme_dark))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        navController = rememberNavController(),
        MutableStateFlow(10000L),
        MutableStateFlow(Theme.SYSTEM),
        { _ -> },
        { _ -> }
    )
}
