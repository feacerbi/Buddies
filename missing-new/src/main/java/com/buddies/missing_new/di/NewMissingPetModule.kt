package com.buddies.missing_new.di

import com.buddies.common.navigation.Navigator
import com.buddies.missing_new.navigation.NewMissingPetNavigator
import com.buddies.missing_new.navigation.NewMissingPetNavigator.Companion.NEW_MISSING_PET_NAVIGATOR_NAME
import com.buddies.missing_new.usecase.NewMissingPetUseCases
import com.buddies.missing_new.viewmodel.NewMissingPetViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val newMissingPetModule = module {
    factory<Navigator>(named(NEW_MISSING_PET_NAVIGATOR_NAME)) { NewMissingPetNavigator() }
    factory { NewMissingPetUseCases(get(), get()) }
    viewModel { NewMissingPetViewModel(get()) }
}