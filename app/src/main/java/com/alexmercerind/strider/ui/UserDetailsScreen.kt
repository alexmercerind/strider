package com.alexmercerind.strider.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alexmercerind.strider.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen() {
    val state = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = state)
    Scaffold(topBar = {
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
        ) {}
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun UserDetailsScreenPreview() {
    UserDetailsScreen()
}
