package io.github.giorgiomicheli.run.location.di

import io.github.giorgiomicheli.run.domain.LocationObserver
import io.github.giorgiomicheli.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
    singleOf(::AndroidLocationObserver).bind<LocationObserver>()
}