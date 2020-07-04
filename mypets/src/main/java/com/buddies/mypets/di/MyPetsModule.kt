package com.buddies.mypets.di

import com.buddies.mypets.usecase.MyPetsUseCases
import org.koin.dsl.module

val myPetsModule = module {
    factory { MyPetsUseCases(get()) }
}