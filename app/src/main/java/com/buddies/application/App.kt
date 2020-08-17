package com.buddies.application

import android.app.Application
import com.buddies.common.di.commonModule
import com.buddies.login.di.loginModule
import com.buddies.mypets.di.petModule
import com.buddies.navigation.di.navigationModule
import com.buddies.profile.di.profileModule
import com.buddies.scanner.di.scannerModule
import com.buddies.security.di.securityModule
import com.buddies.server.di.serverModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@ExperimentalCoroutinesApi
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setUpKoin()
    }

    private fun setUpKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                commonModule,
                serverModule,
                loginModule,
                petModule,
                navigationModule,
                profileModule,
                scannerModule,
                securityModule
            )
        }
    }
}