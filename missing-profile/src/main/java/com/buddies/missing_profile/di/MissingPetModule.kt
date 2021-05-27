package com.buddies.missing_profile.di

import com.buddies.missing_profile.usecase.MissingPetUseCases
import com.buddies.missing_profile.viewmodel.MissingPetProfileViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
val missingPetModule = module {
    factory { MissingPetUseCases(get(), get()) }
    viewModel { params -> MissingPetProfileViewModel(params[0], get(), get(), Dispatchers.Main) }
}