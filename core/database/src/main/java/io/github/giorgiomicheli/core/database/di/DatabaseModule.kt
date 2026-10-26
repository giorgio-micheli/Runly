package io.github.giorgiomicheli.core.database.di

import androidx.room.Room
import io.github.giorgiomicheli.core.database.RoomLocalRunDatasource
import io.github.giorgiomicheli.core.database.RunDatabase
import io.github.giorgiomicheli.core.domain.run.LocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            "runly.db"
        ).build()
    }
    single { get<RunDatabase>().runDao }

    singleOf(::RoomLocalRunDatasource).bind<LocalRunDataSource>()
}