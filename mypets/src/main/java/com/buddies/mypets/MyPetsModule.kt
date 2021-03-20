package com.buddies.mypets

import org.koin.dsl.module

val myPetsModule = module {
    factory { MyPetsUseCases(get()) }
}