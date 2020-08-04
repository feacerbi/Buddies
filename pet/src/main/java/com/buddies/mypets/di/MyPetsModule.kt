package com.buddies.mypets.di

import com.buddies.mypets.usecase.PetUseCases
import com.buddies.mypets.viewmodel.PetProfileViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myPetsModule = module {
    factory { PetUseCases(get(), get()) }
    viewModel { params -> PetProfileViewModel(params[0], get(), Dispatchers.Main) }
}