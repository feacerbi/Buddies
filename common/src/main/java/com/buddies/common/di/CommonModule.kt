package com.buddies.common.di

import coil.ImageLoader
import com.buddies.common.util.ImageHandler
import com.buddies.common.util.ImageHandler.Companion.MEMORY_POOL_PERCENTAGE
import org.koin.dsl.module

val commonModule = module {
    single {
        ImageLoader.Builder(get())
            .availableMemoryPercentage(MEMORY_POOL_PERCENTAGE)
            .build()
    }
    single { ImageHandler(get()) }
}