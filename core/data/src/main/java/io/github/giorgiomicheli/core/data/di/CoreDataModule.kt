package io.github.giorgiomicheli.core.data.di

import io.github.giorgiomicheli.core.data.networking.HttpClientFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory().build()
    }
}