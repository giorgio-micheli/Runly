package io.github.giorgiomicheli.analytics.data.di

import io.github.giorgiomicheli.analytics.data.RoomAnalyticsRepository
import io.github.giorgiomicheli.analytics.domain.AnalyticsRepository
import io.github.giorgiomicheli.core.database.RunDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
    single {
        get<RunDatabase>().analyticsDao
    }
}