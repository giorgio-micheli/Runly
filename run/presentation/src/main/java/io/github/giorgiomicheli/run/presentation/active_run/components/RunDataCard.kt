package io.github.giorgiomicheli.run.presentation.active_run.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.ui.formatted
import io.github.giorgiomicheli.core.presentation.ui.toFormattedKM
import io.github.giorgiomicheli.core.presentation.ui.toFormattedPace
import io.github.giorgiomicheli.run.domain.RunData
import io.github.giorgiomicheli.run.presentation.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun RunDataCard(
    modifier: Modifier = Modifier,
    elapsedTime: Duration,
    runData: RunData
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RunDataItem(
            title = stringResource(id = R.string.duration),
            value = elapsedTime.formatted(),
            valueFontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            RunDataItem(
                modifier = Modifier
                    .defaultMinSize(minWidth = 75.dp),
                title = stringResource(id = R.string.distance),
                value = (runData.distanceMeters / 1000.0).toFormattedKM(),
            )
            RunDataItem(
                modifier = Modifier
                    .defaultMinSize(minWidth = 75.dp),
                title = stringResource(id = R.string.pace),
                value = elapsedTime.toFormattedPace(
                    distanceKM = (runData.distanceMeters / 1000.0)
                ),
            )
        }
    }
}

@Composable
private fun RunDataItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    valueFontSize: TextUnit = 16.sp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = valueFontSize
        )
    }
}

@Preview
@Composable
fun RunDataCardPreview() {
    RunlyTheme {
        RunDataCard(
            elapsedTime = 10.minutes,
            runData = RunData(
                distanceMeters = 4321,
                pace = 3.minutes
            )
        )
    }
}