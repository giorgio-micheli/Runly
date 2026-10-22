@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.giorgiomicheli.run.presentation.run_overview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.giorgiomicheli.core.presentation.designsystem.AnalyticsIcon
import io.github.giorgiomicheli.core.presentation.designsystem.LogoIcon
import io.github.giorgiomicheli.core.presentation.designsystem.LogoutIcon
import io.github.giorgiomicheli.core.presentation.designsystem.RunIcon
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyFloatingActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyScaffold
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyToolbar
import io.github.giorgiomicheli.core.presentation.designsystem.components.util.DropDownItem
import io.github.giorgiomicheli.core.presentation.ui.ObserveAsEvents
import io.github.giorgiomicheli.core.presentation.ui.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    viewModel: RunOverviewViewModel = koinViewModel(),
    onActiveRunButtonClicked: () -> Unit
) {

    ObserveAsEvents(viewModel.events) {event ->
        when(event) {
            is RunOverviewEvent.ActiveRunButtonClicked -> onActiveRunButtonClicked()
        }
    }

    RunOverviewScreen(
        onAction = viewModel::onAction
    )
}

@Composable
private fun RunOverviewScreen(
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    RunlyScaffold(
        topAppBar = {
            RunlyToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.runly),
                scrollBehavior = scrollBehavior,
                menuItems = listOf(
                    DropDownItem(
                        icon = AnalyticsIcon,
                        title = stringResource(id = R.string.analytics)
                    ),
                    DropDownItem(
                        icon = LogoutIcon,
                        title = stringResource(id = R.string.logout)
                    )
                ),
                onMenuItemClick = { index ->
                    when(index) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnLogoutClick)
                    }

                },
                startContent = {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        floatingActionButton = {
            RunlyFloatingActionButton(
                icon = RunIcon,
                onClick = {
                    onAction(RunOverviewAction.OnStartClick)
                },
                isRunning = false
            )
        }
    ) { padding ->


    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunlyTheme {
        RunOverviewScreen(
            onAction = {}
        )
    }
}