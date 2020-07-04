package com.buddies.application

import android.app.Application
import com.buddies.login.di.loginModule
import com.buddies.mypets.di.myPetsModule
import com.buddies.navigation.di.navigationModule
import com.buddies.profile.di.profileModule
import com.buddies.server.di.serverModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

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
                serverModule,
                loginModule,
                myPetsModule,
                navigationModule,
                profileModule
            )
        }
    }
}