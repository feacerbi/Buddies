package com.buddies.server.api

import android.net.Uri
import com.buddies.server.model.Picture
import com.buddies.server.repository.GalleryRepository
import com.buddies.server.util.toStoragePictures

class GalleryApi(
    private val galleryRepository: GalleryRepository
) : BaseApi() {

    suspend fun getPetGalleryPictures(
        petId: String
    ) = runWithResult {
        galleryRepository.listGalleryPictures(petId)
            .handleTaskResult()
            .toStoragePictures()
            .map {
                Picture(it.id, it.downloadTask.handleTaskResult())
            }
    }

    suspend fun addPetGalleryPicture(
        petId: String,
        picture: Uri
    ) = runWithResult {
        galleryRepository.uploadGalleryImage(petId, picture)
            .handleTaskResult()

        null
    }

    suspend fun deletePetGalleryPictures(
        petId: String,
        pictureIdList: List<String>
    ) = runWithResult {
        pictureIdList.forEach {
            galleryRepository.deleteGalleryImage(petId, it)
                .handleNullTaskResult()
        }
    }

}