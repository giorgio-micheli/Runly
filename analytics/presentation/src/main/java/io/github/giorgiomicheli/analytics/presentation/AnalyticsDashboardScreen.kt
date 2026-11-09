package io.github.giorgiomicheli.analytics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.giorgiomicheli.analytics.presentation.components.AnalyticsCard
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyScaffold
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyToolbar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsDashboardScreenRoot(
    viewModel: AnalyticsDashboardViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    AnalyticsDashboardScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                AnalyticsAction.OnBackClick -> onBackClick()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsDashboardScreen(
    state: AnalyticsDashboardState?,
    onAction: (AnalyticsAction) -> Unit
) {
    RunlyScaffold(
        topAppBar = {
            RunlyToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.analytics),
                onBackClick = {
                    onAction(AnalyticsAction.OnBackClick)
                }
            )
        }
    ) { padding ->
        if (state == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AnalyticsCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.total_distance_run),
                        value = state.totalDistanceRun
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AnalyticsCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.total_time_run),
                        value = state.totalTimeRun
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AnalyticsCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.fastest_ever_run),
                        value = state.fastestEverRun
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AnalyticsCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.avg_distance_per_run),
                        value = state.avgDistance
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AnalyticsCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.avg_pace_per_run),
                        value = state.avgPace
                    )
                }
            }
        }

    }
}

@Preview
@Composable
private fun AnalyticsDashboardScreenPreview() {
    RunlyTheme {
        AnalyticsDashboardScreen(
            state = AnalyticsDashboardState(
                totalDistanceRun = "0.2 Km",
                totalTimeRun = "0d 0h 0m",
                fastestEverRun = "143.9 Km/h",
                avgDistance = "0.1 Km",
                avgPace = "07:10"
            ),
            onAction = {}
        )
    }
}