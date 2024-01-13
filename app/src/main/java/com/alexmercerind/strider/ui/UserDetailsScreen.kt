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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alexmercerind.strider.R
import com.alexmercerind.strider.enum.Gender
import com.alexmercerind.strider.ui.navigation.Destinations
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    navController: NavController,
    nameValue: String = "",
    genderValue: Gender? = null,
    heightValue: Float = 0.0F,
    weightValue: Float = 0.0F,
    onSave: (String, Gender?, Float, Float) -> Boolean
) {
    var name by remember { mutableStateOf(nameValue.ifBlank { "" }) }
    var gender by remember { mutableStateOf(genderValue) }
    var height by remember { mutableStateOf(if (heightValue > 0.0F) heightValue.toString() else "") }
    var weight by remember { mutableStateOf(if (weightValue > 0.0F) weightValue.toString() else "") }

    var errored by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState, snapAnimationSpec = null
    )

    Scaffold(topBar = {
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

            val male = stringResource(id = R.string.unit_gender_male)
            val female = stringResource(id = R.string.unit_gender_female)
            val unspecified = stringResource(id = R.string.unit_gender_unspecified)

            ExposedDropdownMenuBox(modifier = Modifier.fillMaxWidth(),
                expanded = false,
                onExpandedChange = { expanded = it }) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    value = when (gender) {
                        Gender.MALE -> male
                        Gender.FEMALE -> female
                        Gender.UNSPECIFIED -> unspecified
                        else -> ""
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.quantity_gender)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                DropdownMenu(
                    modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = true),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        text = { Text(text = male) },
                        onClick = { gender = Gender.MALE; expanded = false }
                    )
                    DropdownMenuItem(contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        text = { Text(text = female) },
                        onClick = { gender = Gender.FEMALE; expanded = false }
                    )
                    DropdownMenuItem(contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        text = { Text(text = unspecified) },
                        onClick = { gender = Gender.UNSPECIFIED; expanded = false }
                    )
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

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                coroutineScope.launch {
                    try {
                        val success = onSave(name, gender, height.toFloat(), weight.toFloat())
                        if (success) {
                            keyboard?.hide()

                            navController.navigateAndReplaceStartDestination(Destinations.Companion.HomeScreen.route)

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

// https://stackoverflow.com/a/69456787/12825435

fun NavController.navigateAndReplaceStartDestination(route: String) {
    popBackStack(graph.startDestinationId, true)
    graph.setStartDestination(route)
    navigate(route)
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun UserDetailsScreenPreview() {
    UserDetailsScreen(
        navController = rememberNavController(),
        nameValue = "Alex",
        genderValue = Gender.MALE,
        heightValue = 168.0F,
        weightValue = 54.0F
    ) { _, _, _, _ ->
        false
    }
}
