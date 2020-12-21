package com.buddies.gallery.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.buddies.common.model.GALLERY_UPLOAD_NOTIFICATION_CHANNEL_ID
import com.buddies.gallery.R
import com.buddies.gallery.receiver.GalleryUploadNotificationReceiver
import com.buddies.gallery.receiver.GalleryUploadNotificationReceiver.Companion.CANCEL_GALLERY_UPLOAD_WORK_ACTION

class GalleryUploadNotificationHandler(
    private val appContext: Context
) {
    private fun progressNotificationBuilder(): NotificationCompat.Builder =
        NotificationCompat.Builder(appContext, GALLERY_UPLOAD_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.buddies_logo_white)
            .setContentTitle(appContext.resources.getString(R.string.gallery_upload_notification_title))
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

    fun showStart(pictures: Int) = with (progressNotificationBuilder()) {
        setContentText(
            appContext.resources.getQuantityString(
                R.plurals.gallery_upload_waiting_notification_content,
                pictures,
                pictures))
        setProgress(0, 0, false)
        addAction(0, appContext.resources.getString(R.string.cancel_button), createCancelUploadPendingIntent(appContext))
        show(build())
    }

    fun updateProgress(max: Int, current: Int) = with (progressNotificationBuilder()) {
        setContentText(appContext.resources.getString(R.string.gallery_upload_progress_notification_content).format(current + 1, max))
        setProgress(max - 1, current, false)
        addAction(0, appContext.resources.getString(R.string.cancel_button), createCancelUploadPendingIntent(appContext))
        show(build())
    }

    fun showComplete() = with (progressNotificationBuilder()) {
        setContentText(
            appContext.resources.getString(R.string.gallery_upload_complete_notification_content))
        setProgress(0, 0, false)
        show(build())
    }

    fun showFail() = with (progressNotificationBuilder()) {
        setContentText(appContext.resources.getString(R.string.gallery_upload_fail_notification_content))
        setProgress(0, 0, false)
        show(build())
    }

    fun cancel() {
        NotificationManagerCompat
            .from(appContext)
            .cancel(NOTIFICATION_ID)
    }

    private fun createCancelUploadIntent(context: Context) =
        Intent(context, GalleryUploadNotificationReceiver::class.java).apply {
            action = CANCEL_GALLERY_UPLOAD_WORK_ACTION
        }

    private fun createCancelUploadPendingIntent(context: Context) =
        PendingIntent.getBroadcast(context, 1, createCancelUploadIntent(context), 0)

    private fun show(notification: Notification) {
        NotificationManagerCompat
            .from(appContext)
            .notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 1
    }
}