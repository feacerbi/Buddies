package com.buddies.common.di

import com.buddies.common.util.PermissionsManager
import org.koin.dsl.module

val commonModule = module {
    factory { PermissionsManager(get()) }
}