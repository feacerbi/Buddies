package com.buddies.gallery.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.buddies.gallery.data.model.GalleryUploadWorkEntity
import com.buddies.gallery.data.model.GalleryUploadWorkEntity.Companion.GALLERY_UPLOAD_WORKS_TABLE_NAME

@Dao
interface GalleryUploadWorksDAO {

    @Query("SELECT * FROM $GALLERY_UPLOAD_WORKS_TABLE_NAME")
    suspend fun getWorks(): List<GalleryUploadWorkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorks(work: List<GalleryUploadWorkEntity>)

    @Delete
    suspend fun deleteWork(work: GalleryUploadWorkEntity)

    @Delete
    suspend fun deleteWorks(work: List<GalleryUploadWorkEntity>)

}