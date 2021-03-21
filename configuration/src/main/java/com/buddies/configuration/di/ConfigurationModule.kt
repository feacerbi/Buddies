package com.buddies.configuration.di

import com.buddies.configuration.Configuration
import org.koin.dsl.module

val configurationModule = module {
    single { Configuration }
}