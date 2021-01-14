package com.buddies.generator.di

import com.buddies.generator.usecase.GeneratorUseCases
import com.buddies.generator.util.ClipboardHelper
import com.buddies.generator.util.QRCodeHelper
import com.buddies.generator.viewmodel.GeneratorViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val generatorModule = module {
    factory { QRCodeHelper(androidApplication()) }
    factory { ClipboardHelper(androidApplication()) }
    factory { GeneratorUseCases(get(), get(), get(), get()) }
    viewModel { GeneratorViewModel(get()) }
}