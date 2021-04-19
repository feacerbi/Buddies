package com.buddies.common.di

import coil.ImageLoader
import coil.request.CachePolicy
import com.buddies.common.util.ImageHandler
import com.buddies.common.util.ImageHandler.Companion.MEMORY_POOL_PERCENTAGE
import com.buddies.common.util.LocationConverter
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val commonModule = module {
    single {
        ImageLoader.Builder(get())
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .availableMemoryPercentage(MEMORY_POOL_PERCENTAGE)
            .build()
    }
    single { ImageHandler(get()) }
    factory { LocationConverter(androidApplication()) }
}