package io.github.giorgiomicheli.run.presentation.di

import io.github.giorgiomicheli.run.presentation.active_run.ActiveRunViewModel
import io.github.giorgiomicheli.run.presentation.run_overview.RunOverviewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val runViewModelModule = module {
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}