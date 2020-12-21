package com.buddies.gallery.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.buddies.common.model.DefaultError
import com.buddies.common.util.safeLaunch
import com.buddies.gallery.usecase.GalleryUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.java.KoinJavaComponent.get

class GalleryUploadNotificationReceiver : BroadcastReceiver() {

    private val galleryUseCases = get(GalleryUseCases::class.java)

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == CANCEL_GALLERY_UPLOAD_WORK_ACTION) {
            coroutineScope.safeLaunch(::handleError) {
                galleryUseCases.cancelUploadWork()
            }
        }
    }

    private fun handleError(defaultError: DefaultError) {
        // Nothing to do
    }

    companion object {
        const val CANCEL_GALLERY_UPLOAD_WORK_ACTION = "com.buddies.action.cancel_gallery_upload_work"
    }
}