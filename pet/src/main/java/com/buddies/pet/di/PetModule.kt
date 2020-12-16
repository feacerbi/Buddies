package com.buddies.pet.di

import androidx.room.Room
import androidx.work.WorkManager
import com.buddies.pet.data.GalleryUploadWorksDatabase
import com.buddies.pet.usecase.PetUseCases
import com.buddies.pet.viewmodel.PetProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val petModule = module {
    single {
        Room.databaseBuilder(androidApplication(), GalleryUploadWorksDatabase::class.java,
            GalleryUploadWorksDatabase.GALLERY_UPLOAD_WORKS_DATABASE_NAME
        ).build()
    }
    single { get<GalleryUploadWorksDatabase>().worksDao() }
    factory { WorkManager.getInstance(get()) }
    factory { PetUseCases(get(), get(), get(), get()) }
    viewModel { params -> PetProfileViewModel(params[0], get(), Dispatchers.Main) }
}