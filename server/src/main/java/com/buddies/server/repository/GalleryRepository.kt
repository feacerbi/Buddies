package com.buddies.server.repository

import android.net.Uri
import com.buddies.common.util.generateNewId
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class GalleryRepository {

    private val storage = Firebase.storage

    fun uploadGalleryImage(
        petId: String,
        photoUri: Uri
    ): UploadTask =
        storage.getReference(PETS_PATH)
            .child(petId)
            .child(GALLERY_PATH)
            .child(generateNewId())
            .putFile(photoUri)

    fun listGalleryPictures(
        petId: String
    ): Task<ListResult> =
        storage.getReference(PETS_PATH)
            .child(petId)
            .child(GALLERY_PATH)
            .listAll()

    fun deleteGalleryImage(
        petId: String,
        photoId: String
    ): Task<Void> =
        storage.getReference(PETS_PATH)
            .child(petId)
            .child(GALLERY_PATH)
            .child(photoId)
            .delete()

    fun deleteGallery(
        petId: String
    ): Task<Void> =
        storage.getReference(PETS_PATH)
            .child(petId)
            .delete()

    companion object {
        private const val PETS_PATH = "pets"
        private const val GALLERY_PATH = "gallery"
    }
}