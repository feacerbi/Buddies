package com.buddies.mypets.di

import com.buddies.mypets.usecase.PetUseCases
import com.buddies.mypets.viewmodel.PetProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val petModule = module {
    factory { PetUseCases(get(), get()) }
    viewModel { params -> PetProfileViewModel(params[0], get(), Dispatchers.Main) }
}