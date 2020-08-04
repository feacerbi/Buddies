package com.buddies.server.di

import com.buddies.server.api.AnimalAPI
import com.buddies.server.api.LoginApi
import com.buddies.server.api.PetApi
import com.buddies.server.api.ProfileApi
import com.buddies.server.repository.*
import org.koin.dsl.module

val serverModule = module {
    single { UsersRepository() }
    single { PetsRepository() }
    single { OwnershipsRepository() }
    single { AnimalsRepository() }
    single { BreedsRepository() }

    single { LoginApi(get()) }
    single { ProfileApi(get()) }
    single { PetApi(get(), get(), get(), get(), get()) }
    single { AnimalAPI(get(), get()) }
}