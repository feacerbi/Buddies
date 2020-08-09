package com.buddies.server.di

import com.buddies.server.api.*
import com.buddies.server.repository.*
import org.koin.dsl.module

val serverModule = module {
    single { UsersRepository() }
    single { PetsRepository() }
    single { OwnershipsRepository() }
    single { AnimalsRepository() }
    single { BreedsRepository() }
    single { NotificationsRepository() }

    single { LoginApi(get()) }
    single { ProfileApi(get(), get(), get(), get()) }
    single { PetApi(get(), get(), get(), get(), get(), get()) }
    single { AnimalApi(get(), get()) }
    single { NotificationsApi(get(), get(), get()) }
}