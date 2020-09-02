package com.buddies.scanner.di

import com.buddies.common.navigation.Navigator
import com.buddies.scanner.navigation.NewPetNavigator
import com.buddies.scanner.navigation.NewPetNavigator.Companion.NEW_PET_NAVIGATOR_NAME
import com.buddies.scanner.usecase.NewPetUseCases
import com.buddies.scanner.viewmodel.NewPetViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val newPetModule = module {
    factory<Navigator>(named(NEW_PET_NAVIGATOR_NAME)) { NewPetNavigator() }
    factory { NewPetUseCases(get(), get()) }
    viewModel { NewPetViewModel(get()) }
}