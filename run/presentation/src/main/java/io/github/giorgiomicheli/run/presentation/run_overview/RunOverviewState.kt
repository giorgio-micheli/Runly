package io.github.giorgiomicheli.run.presentation.run_overview

import io.github.giorgiomicheli.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList()
)
