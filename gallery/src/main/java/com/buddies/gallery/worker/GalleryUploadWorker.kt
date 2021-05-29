package com.buddies.gallery.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.buddies.common.model.GALLERY_UPLOAD_NOTIFICATION_CHANNEL_ID
import com.buddies.common.util.createNotificationChannel
import com.buddies.gallery.R
import com.buddies.gallery.usecase.GalleryUseCases
import com.buddies.gallery.util.GalleryUploadNotificationHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class GalleryUploadWorker(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams), KoinComponent {

    private val galleryUseCases = get<GalleryUseCases>()
    private val notificationHandler = get<GalleryUploadNotificationHandler>()

    init {
        createRequiredNotificationChannels()
    }

    override suspend fun doWork(): Result {
        try {
            val works = galleryUseCases.getGalleryUploadWorks()

            // TODO Optimize images

            works.forEachIndexed { index, entity ->
                notificationHandler.updateProgress(works.size, index)

                galleryUseCases.addPictureToGallery(entity.petId, Uri.parse(entity.pictureUri))
                galleryUseCases.removeUploadWork(entity)
            }
        } catch (exception: Exception) {
            notificationHandler.showFail()

            checkUnsafeUris(exception)
            return Result.failure(workDataOf(KEY_ERROR to exception.message))
        }
        notificationHandler.showComplete()
        return Result.success()
    }

    private fun createRequiredNotificationChannels() {
        createNotificationChannel(
            applicationContext,
            GALLERY_UPLOAD_NOTIFICATION_CHANNEL_ID,
            applicationContext.resources.getString(
                R.string.gallery_upload_notification_channel_name),
            applicationContext.resources.getString(
                R.string.gallery_upload_notification_channel_description)
        )
    }

    private suspend fun checkUnsafeUris(exception: Exception) {
        if (exception is SecurityException) {
            galleryUseCases.clearUploadWorks()
        }
    }

    companion object {
        const val KEY_ERROR = "KEY_ERROR"
    }
}