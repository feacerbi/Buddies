package com.buddies.profile.di

import com.buddies.profile.usecase.ProfileUseCases
import com.buddies.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val profileModule = module {
    factory { ProfileUseCases(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
}