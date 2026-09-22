package io.github.giorgiomicheli.runly

import android.app.Application
import io.github.giorgiomicheli.auth.data.di.authDataModule
import io.github.giorgiomicheli.auth.presentation.di.authViewModelModule
import io.github.giorgiomicheli.runly.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RunlyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RunlyApp)
            modules(
                authDataModule,
                authViewModelModule,
                appModule
            )
        }
    }
}