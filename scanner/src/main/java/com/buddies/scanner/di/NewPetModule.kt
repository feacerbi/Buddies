package com.buddies.scanner.di

import com.buddies.scanner.usecase.NewPetUseCases
import com.buddies.scanner.viewmodel.NewPetViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newPetModule = module {
    factory { NewPetUseCases(get(), get()) }
    viewModel { NewPetViewModel(get()) }
}