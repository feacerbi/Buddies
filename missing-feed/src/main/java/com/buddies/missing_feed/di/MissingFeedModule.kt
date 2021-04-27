package com.buddies.missing_feed.di

import com.buddies.missing_feed.usecase.MissingFeedUseCases
import com.buddies.missing_feed.viewmodel.MissingFeedViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val missingFeedModule = module {
    factory { MissingFeedUseCases(get()) }
    viewModel { MissingFeedViewModel(get(), Dispatchers.Main) }
}