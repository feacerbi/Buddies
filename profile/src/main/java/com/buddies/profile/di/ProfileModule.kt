package com.buddies.profile.di

import com.buddies.profile.usecase.ProfileUseCases
import com.buddies.profile.viewmodel.ProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    factory { ProfileUseCases(get(), get()) }
    viewModel { ProfileViewModel(get()) }
}