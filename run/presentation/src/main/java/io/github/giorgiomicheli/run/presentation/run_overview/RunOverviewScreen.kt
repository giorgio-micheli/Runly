@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.giorgiomicheli.run.presentation.run_overview

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    viewModel: RunOverviewViewModel = koinViewModel()
) {
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