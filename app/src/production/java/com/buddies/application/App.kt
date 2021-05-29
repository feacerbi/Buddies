package com.buddies.application

import android.app.Application
import android.content.Intent
import com.buddies.BuildConfig
import com.buddies.baseModules
import com.buddies.notification.service.NotificationsService
import com.buddies.settings.repository.KeyValueRepository
import com.buddies.settings.repository.KeyValueRepository.StringKey.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
@ExperimentalCoroutinesApi
class App : Application() {

    private val scope = MainScope()
    private val keyValueRepository: KeyValueRepository by inject()

    override fun onCreate() {
        super.onCreate()
        setUpKoin()
        setUpVersionName()
        startService(Intent(applicationContext, NotificationsService::class.java))
    }

    private fun setUpKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(
                *baseModules
            )
        }
    }

    private fun setUpVersionName() = scope.launch {
        keyValueRepository.setStringValue(VERSION_NAME, BuildConfig.VERSION_NAME)
    }
}