package com.buddies.notification.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.buddies.common.model.DefaultError
import com.buddies.common.model.INVITATION_NOTIFICATION_CHANNEL_ID
import com.buddies.common.model.PET_FOUND_NOTIFICATION_CHANNEL_ID
import com.buddies.common.model.UserNotification
import com.buddies.common.util.handleResult
import com.buddies.common.util.safeLaunch
import com.buddies.notification.R
import com.buddies.notification.util.createNotificationChannel
import com.buddies.notification.util.showNotification
import com.buddies.server.api.NotificationsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class NotificationsService : Service() {

    private val notificationsApi: NotificationsApi by inject()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        createRequiredChannels()
        startListenForUnreadNotifications()
    }

    @ExperimentalCoroutinesApi
    private fun startListenForUnreadNotifications() = scope.safeLaunch(::handleError) {
        notificationsApi.listenForCurrentUserNotifications()
            .flowOn(Dispatchers.IO)
            .collect { result ->
                val unreadNotifications = result.handleResult()?.filter {
                    it.unread
                }

                unreadNotifications?.forEach {
                    show(it)
                    markAsRead(it)
                }
            }
    }

    private fun createRequiredChannels() {
        createNotificationChannel(
            this,
            INVITATION_NOTIFICATION_CHANNEL_ID,
            getString(R.string.invitation_notification_channel_name),
            getString(R.string.invitation_notification_channel_description)
        )

        createNotificationChannel(
            this,
            PET_FOUND_NOTIFICATION_CHANNEL_ID,
            getString(R.string.pet_found_notification_channel_name),
            getString(R.string.pet_found_notification_channel_description)
        )
    }

    private fun show(notification: UserNotification) {
        showNotification(
            this@NotificationsService,
            notification.id.hashCode(),
            notification.build(this@NotificationsService))
    }

    private suspend fun markAsRead(
        notification: UserNotification
    ) = withContext(Dispatchers.IO) {
        notificationsApi.markNotificationAsRead(notification.id)
    }

    private fun handleError(error: DefaultError) {
        // Nothing to do
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
