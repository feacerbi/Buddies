package com.buddies.gallery.usecase

import android.net.Uri
import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.buddies.common.usecase.BaseUseCases
import com.buddies.gallery.data.GalleryUploadWorksDAO
import com.buddies.gallery.data.model.GalleryUploadWorkEntity
import com.buddies.gallery.data.model.GalleryUploadWorkEntity.Companion.toGalleryUploadWorkEntity
import com.buddies.gallery.util.GalleryUploadNotificationHandler
import com.buddies.gallery.worker.GalleryUploadWorker
import com.buddies.server.api.GalleryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GalleryUseCases(
    private val galleryApi: GalleryApi,
    private val uploadNotificationHandler: GalleryUploadNotificationHandler,
    private val galleryUploadWorksDAO: GalleryUploadWorksDAO,
    private val workManager: WorkManager
) : BaseUseCases() {

    suspend fun getGalleryPictures(
        petId: String
    ) = request {
        galleryApi.getPetGalleryPictures(petId)
    }

    suspend fun addPictureToGallery(
        petId: String,
        picture: Uri
    ) = request {
        galleryApi.addPetGalleryPicture(petId, picture)
    }

    suspend fun addPicturesToGallery(
        petId: String,
        pictures: List<Uri>
    ) = withContext(Dispatchers.IO) {

        galleryUploadWorksDAO.insertWorks(
            pictures.map { it.toGalleryUploadWorkEntity(petId) }
        )

        uploadNotificationHandler.showStart(galleryUploadWorksDAO.getWorks().size)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequest.Builder(GalleryUploadWorker::class.java)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            GALLERY_UPLOAD_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request)
    }

    suspend fun deletePicturesFromGallery(
        petId: String,
        pictureIds: List<String>
    ) = withContext(Dispatchers.IO) {
        galleryApi.deletePetGalleryPictures(petId, pictureIds)
    }

    suspend fun cancelUploadWork() {
        clearUploadWorks()
        uploadNotificationHandler.cancel()
        workManager.cancelUniqueWork(GALLERY_UPLOAD_WORK_NAME)
    }

    suspend fun getGalleryUploadWorks() = withContext(Dispatchers.IO) {
        galleryUploadWorksDAO.getWorks()
    }

    suspend fun removeUploadWork(work: GalleryUploadWorkEntity) = withContext(Dispatchers.IO) {
        galleryUploadWorksDAO.deleteWork(work)
    }

    suspend fun clearUploadWorks() = withContext(Dispatchers.IO) {
        galleryUploadWorksDAO.deleteWorks(galleryUploadWorksDAO.getWorks())
    }

    fun getUploadWorkStatus(): Flow<WorkInfo.State> =
        workManager.getWorkInfosForUniqueWorkLiveData(GALLERY_UPLOAD_WORK_NAME)
            .asFlow()
            .map { if (it.size > 0) it[0].state else WorkInfo.State.CANCELLED }
            .flowOn(Dispatchers.IO)

    companion object {
        private const val GALLERY_UPLOAD_WORK_NAME = "gallery_upload"
    }
}