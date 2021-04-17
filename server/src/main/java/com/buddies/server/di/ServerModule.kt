package com.buddies.server.di

import com.buddies.server.api.AnimalApi
import com.buddies.server.api.FavoritesApi
import com.buddies.server.api.GeneratorApi
import com.buddies.server.api.HomeApi
import com.buddies.server.api.LoginApi
import com.buddies.server.api.MissingPetApi
import com.buddies.server.api.NewPetApi
import com.buddies.server.api.NotificationsApi
import com.buddies.server.api.PetApi
import com.buddies.server.api.ProfileApi
import com.buddies.server.api.ScannerApi
import com.buddies.server.api.SecurityApi
import com.buddies.server.repository.AnimalsRepository
import com.buddies.server.repository.BreedsRepository
import com.buddies.server.repository.MissingPetsRepository
import com.buddies.server.repository.NotificationsRepository
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.SecurityRepository
import com.buddies.server.repository.TagsRepository
import com.buddies.server.repository.UsersRepository
import org.koin.dsl.module

val serverModule = module {
    single { UsersRepository() }
    single { PetsRepository() }
    single { OwnershipsRepository() }
    single { AnimalsRepository() }
    single { BreedsRepository() }
    single { NotificationsRepository() }
    single { SecurityRepository() }
    single { TagsRepository() }
    single { MissingPetsRepository() }

    single { LoginApi(get()) }
    single { ProfileApi(get()) }
    single { PetApi(get(), get(), get(), get(), get(), get(), get()) }
    single { AnimalApi(get(), get()) }
    single { NotificationsApi(get(), get(), get(), get()) }
    single { SecurityApi(get()) }
    single { NewPetApi(get(), get(), get(), get()) }
    single { HomeApi(get(), get(), get(), get(), get()) }
    single { ScannerApi(get()) }
    single { GeneratorApi(get()) }
    single { FavoritesApi(get()) }
    single { MissingPetApi(get(), get(), get(), get()) }
}