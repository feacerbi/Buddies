package com.buddies.gallery.di

import androidx.room.Room
import androidx.work.WorkManager
import com.buddies.gallery.data.GalleryUploadWorksDatabase
import com.buddies.gallery.usecase.GalleryUseCases
import com.buddies.gallery.util.GalleryUploadNotificationHandler
import com.buddies.gallery.viewmodel.GalleryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val galleryModule = module {
    single {
        Room.databaseBuilder(androidApplication(), GalleryUploadWorksDatabase::class.java,
            GalleryUploadWorksDatabase.GALLERY_UPLOAD_WORKS_DATABASE_NAME
        ).build()
    }
    single { GalleryUploadNotificationHandler(androidApplication()) }
    single { get<GalleryUploadWorksDatabase>().worksDao() }
    factory { WorkManager.getInstance(get()) }
    factory { GalleryUseCases(get(), get(), get(), get()) }
    viewModel { params -> GalleryViewModel(params[0], get(), Dispatchers.Main) }
}