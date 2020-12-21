package com.buddies.gallery.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buddies.gallery.data.model.GalleryUploadWorkEntity.Companion.GALLERY_UPLOAD_WORKS_TABLE_NAME
import java.util.*

@Entity(tableName = GALLERY_UPLOAD_WORKS_TABLE_NAME)
data class GalleryUploadWorkEntity(
    @PrimaryKey @ColumnInfo(name = WORK_ID_COLUMN_NAME) val id: String,
    @ColumnInfo(name = WORK_PET_ID_COLUMN_NAME) val petId: String,
    @ColumnInfo(name = WORK_PICTURE_URI_COLUMN_NAME) val pictureUri: String,
) {
    companion object {
        const val GALLERY_UPLOAD_WORKS_TABLE_NAME = "gallery_upload_works"
        const val WORK_ID_COLUMN_NAME = "work_id"
        const val WORK_PET_ID_COLUMN_NAME = "work_pet_id"
        const val WORK_PICTURE_URI_COLUMN_NAME = "work_pet_uri"

        fun Uri.toGalleryUploadWorkEntity(petId: String) = GalleryUploadWorkEntity(
            UUID.randomUUID().toString(),
            petId,
            this.toString()
        )
    }
}
