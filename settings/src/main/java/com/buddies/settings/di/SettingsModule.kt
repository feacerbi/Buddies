package com.buddies.settings.di

import com.buddies.settings.repository.KeyValueRepository
import org.koin.dsl.module

val settingsModule = module {
    single { KeyValueRepository(get()) }
}