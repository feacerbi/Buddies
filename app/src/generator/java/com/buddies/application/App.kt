package com.buddies.application

import android.app.Application
import android.content.Intent
import com.buddies.baseModules
import com.buddies.generator.di.generatorModule
import com.buddies.notification.service.NotificationsService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

@ExperimentalCoroutinesApi
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setUpKoin()
        startService(Intent(applicationContext, NotificationsService::class.java))
    }

    private fun setUpKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(
                *baseModules + generatorModule
            )
        }
    }
}