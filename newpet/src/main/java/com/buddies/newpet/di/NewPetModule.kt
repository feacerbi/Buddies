package com.buddies.newpet.di

import com.buddies.common.navigation.Navigator
import com.buddies.newpet.navigation.NewPetNavigator
import com.buddies.newpet.navigation.NewPetNavigator.Companion.NEW_PET_NAVIGATOR_NAME
import com.buddies.newpet.usecase.NewPetUseCases
import com.buddies.newpet.viewmodel.NewPetViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val newPetModule = module {
    factory<Navigator>(named(NEW_PET_NAVIGATOR_NAME)) { NewPetNavigator() }
    factory { NewPetUseCases(get(), get()) }
    viewModel { NewPetViewModel(get()) }
}