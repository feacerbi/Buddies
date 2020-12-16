package com.buddies.pet.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.buddies.pet.model.GalleryUploadWorkEntity

@Database(entities = [GalleryUploadWorkEntity::class], version = 1, exportSchema = false)
abstract class GalleryUploadWorksDatabase : RoomDatabase() {

    abstract fun worksDao(): GalleryUploadWorksDAO

    companion object {
        const val GALLERY_UPLOAD_WORKS_DATABASE_NAME = "GalleryUploadWorksDatabase"
    }
}