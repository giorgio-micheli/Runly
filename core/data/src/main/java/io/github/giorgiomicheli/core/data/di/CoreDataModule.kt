package io.github.giorgiomicheli.core.data.di

import android.content.SharedPreferences
import io.github.giorgiomicheli.core.data.auth.EncryptedSessionStorage
import io.github.giorgiomicheli.core.data.networking.HttpClientFactory
import io.github.giorgiomicheli.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory().build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
}