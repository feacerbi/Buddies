package com.buddies.login.di

import com.buddies.login.usecase.LoginUseCases
import com.buddies.login.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    factory { LoginUseCases(get()) }
    viewModel { LoginViewModel(get(), get()) }
}