package com.buddies.server.di

import com.buddies.server.api.LoginApi
import com.buddies.server.api.MyPetsApi
import com.buddies.server.api.ProfileApi
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.UsersRepository
import org.koin.dsl.module

val serverModule = module {
    single { UsersRepository() }
    single { PetsRepository() }
    single { OwnershipsRepository() }

    single { LoginApi(get()) }
    single { ProfileApi(get()) }
    single { MyPetsApi(get(), get(), get()) }
}