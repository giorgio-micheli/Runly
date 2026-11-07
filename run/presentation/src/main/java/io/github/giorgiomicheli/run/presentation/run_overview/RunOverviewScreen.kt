@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.giorgiomicheli.run.presentation.run_overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import io.github.giorgiomicheli.run.presentation.run_overview.component.RunListItem
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.getScopeId

@Composable
fun RunOverviewScreenRoot(
    viewModel: RunOverviewViewModel = koinViewModel(),
    onActiveRunButtonClicked: () -> Unit,
    onLogoutClick: () -> Unit
) {

    ObserveAsEvents(viewModel.events) {event ->
        when(event) {
            is RunOverviewEvent.ActiveRunButtonClicked -> onActiveRunButtonClicked()
            is RunOverviewEvent.LogoutButtonClicked -> onLogoutClick()
        }
    }

    RunOverviewScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun RunOverviewScreen(
    state: RunOverviewState,
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
                enabled = true,
                isRunning = false
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 20.dp),
            contentPadding = padding.plus(PaddingValues(bottom = 20.dp)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = state.runs,
                key = { it.id }
            ) {
                RunListItem(
                    modifier = Modifier
                        .animateItem(),
                    runUi = it,
                    onDeleteClick = {
                        onAction(RunOverviewAction.DeleteRun(it))
                    },
                )
            }
        }

    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunlyTheme {
        RunOverviewScreen(
            state = RunOverviewState(),
            onAction = {}
        )
    }
}