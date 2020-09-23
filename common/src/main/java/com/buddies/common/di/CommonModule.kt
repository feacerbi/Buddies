package com.buddies.common.di

import coil.ImageLoader
import com.buddies.common.util.ImageCache
import com.buddies.common.util.PermissionsManager
import org.koin.dsl.module

val commonModule = module {
    factory { PermissionsManager(get()) }
    single {
        ImageLoader.Builder(get())
            .availableMemoryPercentage(0.4)
            .build()
    }
    single { ImageCache(get()) }
}