package com.buddies.application

import android.app.Application
import android.content.Intent
import com.buddies.common.di.commonModule
import com.buddies.login.di.loginModule
import com.buddies.mypets.di.petModule
import com.buddies.navigation.di.navigationModule
import com.buddies.notification.service.CheckNotificationsService
import com.buddies.profile.di.profileModule
import com.buddies.scanner.di.newPetModule
import com.buddies.security.di.securityModule
import com.buddies.server.di.serverModule
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
        startService(Intent(applicationContext, CheckNotificationsService::class.java))
    }

    private fun setUpKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(
                commonModule,
                serverModule,
                loginModule,
                petModule,
                navigationModule,
                profileModule,
                newPetModule,
                securityModule
            )
        }
    }
}