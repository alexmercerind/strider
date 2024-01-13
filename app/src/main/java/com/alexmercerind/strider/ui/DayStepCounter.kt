package com.alexmercerind.strider.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
fun DayStepCounter(current: Long, target: Long) {
    val progress by animateFloatAsState(
        label = "DayStepCounter",
        targetValue = current.toFloat() / target.toFloat(),
        animationSpec = tween(1000, 200, EaseInOut)
    )
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = 1.0F,
            modifier = Modifier
                .fillMaxWidth(0.8F)
                .defaultMinSize(minWidth = 360.dp, minHeight = 360.dp)
                .heightIn(0.dp, 480.dp)
                .widthIn(0.dp, 480.dp)
                .aspectRatio(1.0F),
            strokeWidth = 20.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        )
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
            AnimatedContent(
                label = "DayStepCounter",
                targetState = current,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { height -> height } + fadeIn() togetherWith
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
            ) { current ->
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
            }
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
        current = 420,
        target = 1000
    )
}
