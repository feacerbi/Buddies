package com.buddies.application

import android.app.Application
import android.content.Intent
import com.buddies.common.di.commonModule
import com.buddies.home.di.homeModule
import com.buddies.login.di.loginModule
import com.buddies.mypets.di.petModule
import com.buddies.navigation.di.navigationModule
import com.buddies.newpet.di.newPetModule
import com.buddies.notification.service.NotificationsService
import com.buddies.profile.di.profileModule
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
        startService(Intent(applicationContext, NotificationsService::class.java))
    }

    private fun setUpKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(
                commonModule,
                homeModule,
                loginModule,
                navigationModule,
                newPetModule,
                petModule,
                profileModule,
                securityModule,
                serverModule
            )
        }
    }
}