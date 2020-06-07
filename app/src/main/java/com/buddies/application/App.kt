package com.buddies.application

import android.app.Application
import com.buddies.common.di.commonModule
import com.buddies.login.di.loginModule
import com.buddies.navigation.di.navigationModule
import com.buddies.profile.di.profileModule
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
                commonModule,
                loginModule,
                navigationModule,
                profileModule
            )
        }
    }
}