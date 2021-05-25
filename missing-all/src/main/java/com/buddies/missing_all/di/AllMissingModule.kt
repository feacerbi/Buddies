package com.buddies.missing_all.di

import com.buddies.missing_all.usecase.AllMissingPetsUseCases
import com.buddies.missing_all.viewmodel.AllMissingViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val allMissingModule = module {
    factory { AllMissingPetsUseCases(get()) }
    viewModel { params -> AllMissingViewModel(params[0], get(), Dispatchers.Main) }
}