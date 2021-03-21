package com.buddies.home.di

import com.buddies.home.usecase.HomeUseCases
import com.buddies.home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    factory { HomeUseCases(get()) }
    viewModel { HomeViewModel(get(), get()) }
}