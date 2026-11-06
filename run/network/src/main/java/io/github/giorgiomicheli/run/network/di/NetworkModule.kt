package io.github.giorgiomicheli.run.network.di

import io.github.giorgiomicheli.core.data.run.OfflineFirstRunRepository
import io.github.giorgiomicheli.core.domain.run.RemoteRunDataSource
import io.github.giorgiomicheli.core.domain.run.RunRepository
import io.github.giorgiomicheli.run.network.KtorRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::KtorRemoteDataSource).bind<RemoteRunDataSource>()
}