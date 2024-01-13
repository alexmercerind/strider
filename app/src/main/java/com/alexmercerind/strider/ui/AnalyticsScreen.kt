package com.alexmercerind.strider.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alexmercerind.strider.R
import com.alexmercerind.strider.model.Step
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableWeekCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import kotlin.reflect.KSuspendFunction2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    navController: NavController,
    goal: StateFlow<Long>,
    getStepsInRange: KSuspendFunction2<Instant, Instant, List<Step>>,
    watchStepsInRange: (Instant, Instant) -> LiveData<List<Step>>,
    getStepCountInRange: KSuspendFunction2<Instant, Instant, Long>,
    watchStepCountInRange: (Instant, Instant) -> LiveData<Long>
) {

    val state = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = state, snapAnimationSpec = null)

    val calendarState = rememberSelectableWeekCalendarState(
        selectionState = DynamicSelectionState(
            selection = listOf(LocalDate.now()),
            selectionMode = SelectionMode.Single
        )
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.analytics)) },
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
                SelectableWeekCalendar(
                    calendarState = calendarState,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    weekHeader = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                onClick = { it.currentWeek = it.currentWeek.dec() }
                            ) {
                                Image(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                                    contentDescription = "",
                                )
                            }
                            Spacer(modifier = Modifier.weight(1.0F))
                            Text(
                                text = it.currentWeek.yearMonth.month
                                    .getDisplayName(TextStyle.FULL, Locale.getDefault())
                                    .lowercase()
                                    .replaceFirstChar { it.titlecase() },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = it.currentWeek.yearMonth.year.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.weight(1.0F))
                            IconButton(
                                onClick = { it.currentWeek = it.currentWeek.inc() }
                            ) {
                                Image(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                                    contentDescription = "",
                                )
                            }
                        }
                    },
                    daysOfWeekHeader = {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                        ) {
                            it.forEach {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = it.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                    modifier = Modifier
                                        .weight(1f)
                                        .wrapContentHeight(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    },
                    dayContent = {
                        val date = it.date
                        val state = it.selectionState

                        val isSelected = state.isDateSelected(date)

                        var modifier = Modifier
                            .aspectRatio(1.0F)
                            .padding(2.dp)
                        if (isSelected) {
                            modifier = modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                        }
                        modifier = modifier.clip(CircleShape)

                        Surface(
                            modifier = modifier,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(
                                modifier = Modifier.clickable {
                                    if (!state.isDateSelected(date)) {
                                        state.onDateSelected(date)
                                    }
                                },
                                contentAlignment = Alignment.Center,
                            ) {
                                val from = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                val to = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

                                val p by remember(from, to) {
                                    watchStepCountInRange(from, to)
                                }.observeAsState(0L)

                                val q by goal.collectAsState()

                                CircularProgressIndicator(
                                    progress = (p.toDouble() / q.toDouble()).toFloat()
                                )

                                Text(text = date.dayOfMonth.toString())
                            }
                        }
                    }
                )
            }
            item {
                DayAnalytics(
                    day = calendarState.selectionState.selection.first(),
                    goal = goal,
                    getStepsInRange = getStepsInRange,
                    watchStepsInRange = watchStepsInRange,
                    getStepCountInRange = getStepCountInRange,
                    watchStepCountInRange = watchStepCountInRange
                )
            }
        }
    }
}

@Composable
@Preview
fun AnalyticsScreenPreview() {
    suspend fun fun1(from: Instant, to: Instant) = listOf<Step>()
    suspend fun fun2(from: Instant, to: Instant) = 0L

    AnalyticsScreen(
        navController = rememberNavController(),
        MutableStateFlow(10000L),
        ::fun1,
        { _, _ -> MutableLiveData() },
        ::fun2,
        { _, _ -> MutableLiveData() }
    )
}
