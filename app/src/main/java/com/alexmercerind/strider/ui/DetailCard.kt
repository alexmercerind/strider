package com.alexmercerind.strider.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexmercerind.strider.R

@Composable
fun DetailCard(@DrawableRes icon: Int, headlineText: String, supportingText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp, vertical = 24.dp
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = headlineText,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            AnimatedContent(
                label = "DetailCard",
                targetState = headlineText,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { headlineText ->
                Text(
                    text = headlineText,
                    style = MaterialTheme.typography.displaySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
@Preview
fun DetailCardPreview() {
    DetailCard(
        icon = R.drawable.baseline_map_24,
        headlineText = "${1.8} ${stringResource(id = R.string.unit_kilometre_small)}",
        supportingText = stringResource(id = R.string.label_distance)
    )
}
