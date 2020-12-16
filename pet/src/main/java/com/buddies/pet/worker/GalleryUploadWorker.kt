package com.buddies.pet.worker

import android.app.Notification
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.buddies.common.model.GALLERY_UPLOAD_NOTIFICATION_CHANNEL_ID
import com.buddies.notification.util.createNotificationChannel
import com.buddies.pet.R
import com.buddies.pet.usecase.PetUseCases
import org.koin.core.KoinComponent
import org.koin.core.get

class GalleryUploadWorker(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams), KoinComponent {

    private val petUseCases = get<PetUseCases>()

    private val progressNotificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(applicationContext, GALLERY_UPLOAD_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.buddies_logo_white)
            .setContentTitle(appContext.resources.getString(R.string.gallery_upload_notification_title))
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
    }

    init {
        createRequiredNotificationChannels()
    }

    override suspend fun doWork(): Result {
        try {
            val works = petUseCases.getGalleryUploadWorks()

            // TODO Optimize images

            works.forEachIndexed { index, entity ->
                updateProgressNotification(works.size, index)
                petUseCases.addToGallery(entity.petId, Uri.parse(entity.pictureUri))
                petUseCases.removeUploadWork(entity)
            }
        } catch (exception: Exception) {
            showFailNotification()
            checkUnsafeUris(exception)
            return Result.failure(workDataOf(KEY_ERROR to exception.message))
        }
        showCompleteNotification()
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

    private fun updateProgressNotification(max: Int, current: Int) {
        progressNotificationBuilder.setContentText(
            applicationContext.resources.getString(R.string.gallery_upload_progress_notification_content)
                .format(current + 1, max))
        progressNotificationBuilder.setProgress(max - 1, current, false)
        show(progressNotificationBuilder.build())
    }

    private fun showCompleteNotification() {
        progressNotificationBuilder.setContentText(
            applicationContext.resources.getString(R.string.gallery_upload_complete_notification_content))
        progressNotificationBuilder.setProgress(0, 0, false)
        show(progressNotificationBuilder.build())
    }

    private fun showFailNotification() {
        progressNotificationBuilder.setContentText(
            applicationContext.resources.getString(R.string.gallery_upload_fail_notification_content))
        progressNotificationBuilder.setProgress(0, 0, false)
        show(progressNotificationBuilder.build())
    }

    private fun show(notification: Notification) {
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID, notification)
    }

    private suspend fun checkUnsafeUris(exception: Exception) {
        if (exception is SecurityException) {
            petUseCases.clearUploadWorks()
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val KEY_ERROR = "KEY_ERROR"
    }
}