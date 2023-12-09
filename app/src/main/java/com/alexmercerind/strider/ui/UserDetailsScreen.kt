package com.alexmercerind.strider.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexmercerind.strider.R
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen() {
    val userDetailsViewModel: UserDetailsViewModel = viewModel()

    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    LaunchedEffect("UserDetailsScreen") {
        try {
            if (userDetailsViewModel.name.value.isNotEmpty()) {
                name = userDetailsViewModel.name.value
            }
            if (userDetailsViewModel.gender.value.isNotEmpty()) {
                gender = userDetailsViewModel.gender.value
            }
            if (userDetailsViewModel.height.value > 0.0) {
                height = userDetailsViewModel.height.value.toString()
            }
            if (userDetailsViewModel.weight.value > 0.0) {
                weight = userDetailsViewModel.weight.value.toString()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    var errored by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    val topAppBarState = rememberTopAppBarState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState, snapAnimationSpec = null
    )

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, topBar = {
        LargeTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.user_details))
            }, scrollBehavior = scrollBehavior
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                label = { Text(text = stringResource(id = R.string.user_name)) })
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(modifier = Modifier.fillMaxWidth(),
                expanded = false,
                onExpandedChange = { expanded = it }) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.quantity_gender)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = true),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    val male = stringResource(id = R.string.unit_gender_male)
                    val female = stringResource(id = R.string.unit_gender_female)
                    val unspecified = stringResource(id = R.string.unit_gender_unspecified)
                    DropdownMenuItem(contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        text = { Text(text = male) },
                        onClick = { gender = male; expanded = false })
                    DropdownMenuItem(contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        text = { Text(text = female) },
                        onClick = { gender = female; expanded = false })
                    DropdownMenuItem(contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        text = { Text(text = unspecified) },
                        onClick = { gender = unspecified; expanded = false })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(modifier = Modifier.fillMaxWidth(),
                value = height,
                onValueChange = { height = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                label = {
                    Text(
                        text = "${stringResource(id = R.string.quantity_height)} (${
                            stringResource(
                                id = R.string.unit_centimeter_small
                            )
                        })"
                    )
                })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(modifier = Modifier.fillMaxWidth(),
                value = weight,
                onValueChange = { weight = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                label = {
                    Text(
                        text = "${stringResource(id = R.string.quantity_weight)} (${
                            stringResource(
                                id = R.string.unit_kilogram_small
                            )
                        })"
                    )
                })
            Spacer(modifier = Modifier.height(16.dp))

            val keyboard = LocalSoftwareKeyboardController.current
            val snackbarMessage = stringResource(id = R.string.save_success)
            val snackbarActionLabel = stringResource(id = R.string.ok)

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                coroutineScope.launch {
                    try {
                        val success = userDetailsViewModel.save(
                            name, gender, height.toFloat(), weight.toFloat()
                        )
                        if (success) {
                            async {
                                keyboard?.hide()
                                snackbarHostState.showSnackbar(
                                    message = snackbarMessage, actionLabel = snackbarActionLabel
                                )
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }
                            /*TODO*/
                            return@launch
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                    errored = true
                }
            }) {
                Text(text = stringResource(id = R.string.save))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (errored) {
        AlertDialog(onDismissRequest = { errored = false },
            confirmButton = {
                TextButton(onClick = { errored = false }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            icon = {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = stringResource(id = R.string.warning)
                )
            },
            title = { Text(text = stringResource(id = R.string.save_failure_title)) },
            text = { Text(text = stringResource(id = R.string.save_failure_content)) })
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun UserDetailsScreenPreview() {
    UserDetailsScreen()
}
