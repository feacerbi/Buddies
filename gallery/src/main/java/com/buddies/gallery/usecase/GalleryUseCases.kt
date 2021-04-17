package com.buddies.gallery.usecase

import android.net.Uri
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
import com.buddies.server.api.PetApi
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class GalleryUseCases(
    private val petApi: PetApi,
    private val uploadNotificationHandler: GalleryUploadNotificationHandler,
    private val galleryUploadWorksDAO: GalleryUploadWorksDAO,
    private val workManager: WorkManager
) : BaseUseCases() {

    suspend fun getGalleryPictures(
        petId: String
    ) = request {
        petApi.getPetGalleryPictures(petId)
    }

    suspend fun addPictureToGallery(
        petId: String,
        picture: Uri
    ) = request {
        petApi.addPetGalleryPicture(petId, picture)
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
        petApi.deletePetGalleryPictures(petId, pictureIds)
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

    @ExperimentalCoroutinesApi
    fun getUploadWorkStatus() = callbackFlow<WorkInfo.State> {

        val workInfoCallback = object : FutureCallback<List<WorkInfo>> {
            override fun onSuccess(result: List<WorkInfo>?) {
                sendBlocking(
                    if (result == null || result.isEmpty()) {
                        WorkInfo.State.CANCELLED
                    } else {
                        result[0].state
                    })
            }
            override fun onFailure(t: Throwable) {
                close(t)
            }
        }

        val workInfosFuture = workManager.getWorkInfosForUniqueWork(GALLERY_UPLOAD_WORK_NAME)
        Futures.addCallback(workInfosFuture, workInfoCallback, Executors.newSingleThreadExecutor())

        awaitClose { workInfosFuture.cancel(true) }
    }.flowOn(Dispatchers.Default)

    companion object {
        private const val GALLERY_UPLOAD_WORK_NAME = "gallery_upload"
    }
}