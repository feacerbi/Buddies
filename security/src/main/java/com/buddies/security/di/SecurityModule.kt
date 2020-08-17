package com.buddies.security.di

import com.buddies.security.encryption.Encrypter
import com.buddies.security.usecase.SecurityUseCases
import org.koin.dsl.module

val securityModule = module {
    factory { SecurityUseCases(get()) }
    factory { Encrypter(get()) }
}