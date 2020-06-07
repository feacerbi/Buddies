package com.buddies.common.di

import com.buddies.common.repository.UserRepository
import org.koin.dsl.module

val commonModule = module {
    single { UserRepository() }
}