package com.alexmercerind.strider.ui

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexmercerind.strider.R

@Composable
fun DayStepCounter(current: Int, target: Int) {
    val progress by animateFloatAsState(
        label = "DayStepCounter::progress",
        targetValue = current.toFloat() / target.toFloat(),
        animationSpec = tween(400, 200, EaseInOut)
    )
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth(0.8F)
                .defaultMinSize(minWidth = 360.dp, minHeight = 360.dp)
                .heightIn(0.dp, 480.dp)
                .widthIn(0.dp, 480.dp)
                .aspectRatio(1.0F),
            strokeWidth = 20.dp
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    addStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold
                        ), 0, current.toString().length
                    )
                    append(current.toString())
                    append("/")
                    append(target.toString())
                }, style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.quantity_steps).uppercase(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}


@Composable
@Preview
fun DayStepCounterPreview() {
    DayStepCounter(
        current = 420, target = 1000
    )
}
