package com.buddies.missing.di

import com.buddies.missing.usecase.MissingFeedUseCases
import com.buddies.missing.viewmodel.MissingFeedViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val missingFeedModule = module {
    factory { MissingFeedUseCases(get()) }
    viewModel { MissingFeedViewModel(get(), Dispatchers.Main) }
}