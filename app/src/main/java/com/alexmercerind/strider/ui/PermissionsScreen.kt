package com.alexmercerind.strider.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alexmercerind.strider.R
import com.alexmercerind.strider.ui.navigation.Destinations
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(navController: NavController) {
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

    val physicalActivityPermission by remember {
        derivedStateOf {
            physicalActivityPermissionState?.status?.isGranted ?: false
        }
    }
    val notificationsPermission by remember {
        derivedStateOf {
            notificationsPermissionState?.status?.isGranted ?: false
        }
    }

    val state = rememberTopAppBarState()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = state, snapAnimationSpec = null)
    Scaffold(bottomBar = {
        BottomAppBar(actions = {
            Spacer(modifier = Modifier.weight(1.0F))
        }, floatingActionButton = {
            FloatingActionButton(
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    focusedElevation = 0.dp,
                    hoveredElevation = 0.dp
                ), onClick = {
                    if (physicalActivityPermission && notificationsPermission) {
                        navController.navigate(Destinations.Companion.UserDetailsScreen.route)
                    }
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = stringResource(id = R.string.permissions_physical_activity_headline),
                )
            }
        })
    }, topBar = {
        LargeTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.permissions))
            }, scrollBehavior = scrollBehavior
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(padding)
        ) {
            item {
                ListItem(leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_directions_walk_24),
                        contentDescription = stringResource(id = R.string.permissions_physical_activity_headline),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(32.dp)
                    )
                },
                    headlineContent = { Text(text = stringResource(id = R.string.permissions_physical_activity_headline)) },
                    supportingContent = { Text(text = stringResource(id = R.string.permissions_physical_activity_supporting)) })
            }
            item {
                Button(
                    enabled = !physicalActivityPermission, onClick = {
                        physicalActivityPermissionState?.launchPermissionRequest()
                    }, modifier = Modifier.padding(start = 72.dp)
                ) {
                    Icon(
                        imageVector = if (physicalActivityPermission) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.permissions_physical_activity_headline),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp)
                    )
                    Text(text = stringResource(id = if (physicalActivityPermission) R.string.permissions_granted else R.string.permissions_grant))
                }
            }
            item {
                ListItem(leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_message_24),
                        contentDescription = stringResource(id = R.string.permissions_notifications_headline),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(32.dp)
                    )
                },
                    headlineContent = { Text(text = stringResource(id = R.string.permissions_notifications_headline)) },
                    supportingContent = { Text(text = stringResource(id = R.string.permissions_notifications_supporting)) })
            }
            item {
                Button(
                    enabled = !notificationsPermission, onClick = {
                        notificationsPermissionState?.launchPermissionRequest()
                    }, modifier = Modifier.padding(start = 72.dp)
                ) {
                    Icon(
                        imageVector = if (notificationsPermission) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.permissions_physical_activity_headline),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp)
                    )
                    Text(text = stringResource(id = if (notificationsPermission) R.string.permissions_granted else R.string.permissions_grant))
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PermissionsScreenPreview() {
    PermissionsScreen(
        navController = rememberNavController()
    )
}
