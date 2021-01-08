package com.buddies.application

import android.app.Application
import android.content.Intent
import com.buddies.common.di.commonModule
import com.buddies.gallery.di.galleryModule
import com.buddies.home.di.homeModule
import com.buddies.login.di.loginModule
import com.buddies.navigation.di.navigationModule
import com.buddies.newpet.di.newPetModule
import com.buddies.notification.service.NotificationsService
import com.buddies.pet.di.petModule
import com.buddies.profile.di.profileModule
import com.buddies.scanner.di.scannerModule
import com.buddies.security.di.securityModule
import com.buddies.server.di.serverModule
//import android.util.Log
//import com.buddies.security.encryption.Encrypter
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.launch
//import org.koin.android.ext.android.inject
//import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

@ExperimentalCoroutinesApi
class App : Application() {

//    private val encrypter by inject<Encrypter>()

    override fun onCreate() {
        super.onCreate()
        setUpKoin()
        startService(Intent(applicationContext, NotificationsService::class.java))

//        CoroutineScope(Job() + Dispatchers.Default).launch {
//            val newValue = Random.nextLong(1000000000000000, 9999999999999999)
//            val result = encrypter.encrypt(newValue.toString())
//            Log.d("New Tag", "Value: $newValue | Encrypted: $result")
//        }
    }

    private fun setUpKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(
                commonModule,
                galleryModule,
                homeModule,
                loginModule,
                navigationModule,
                newPetModule,
                petModule,
                profileModule,
                scannerModule,
                securityModule,
                serverModule
            )
        }
    }
}