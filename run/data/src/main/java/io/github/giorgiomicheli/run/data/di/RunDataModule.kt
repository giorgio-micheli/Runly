package io.github.giorgiomicheli.run.data.di

import io.github.giorgiomicheli.core.domain.run.SyncRunScheduler
import io.github.giorgiomicheli.run.data.CreateRunWorker
import io.github.giorgiomicheli.run.data.DeleteRunWorker
import io.github.giorgiomicheli.run.data.FetchRunsWorker
import io.github.giorgiomicheli.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}