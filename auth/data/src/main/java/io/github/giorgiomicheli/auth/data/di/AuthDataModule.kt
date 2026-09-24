package io.github.giorgiomicheli.auth.data.di

import io.github.giorgiomicheli.auth.data.AuthRepositoryImpl
import io.github.giorgiomicheli.auth.data.EmailPatternValidator
import io.github.giorgiomicheli.auth.domain.AuthRepository
import io.github.giorgiomicheli.auth.domain.PatternValidator
import io.github.giorgiomicheli.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}