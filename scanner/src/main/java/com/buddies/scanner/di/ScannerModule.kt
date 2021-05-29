package com.buddies.scanner.di

import com.buddies.scanner.usecase.ScannerUseCases
import com.buddies.scanner.viewmodel.ScannerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val scannerModule = module {
    factory { ScannerUseCases(get()) }
    viewModel { ScannerViewModel(get(), get()) }
}