package io.github.giorgiomicheli.run.data.di

import io.github.giorgiomicheli.run.data.CreateRunWorker
import io.github.giorgiomicheli.run.data.DeleteRunWorker
import io.github.giorgiomicheli.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)
}