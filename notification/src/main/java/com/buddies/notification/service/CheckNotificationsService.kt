package com.buddies.notification.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.buddies.common.model.DefaultError
import com.buddies.common.model.UserNotification
import com.buddies.common.ui.SingleActivity
import com.buddies.common.util.handleResult
import com.buddies.common.util.safeLaunch
import com.buddies.notification.R
import com.buddies.server.api.NotificationsApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class CheckNotificationsService : Service() {

    private val notificationsApi: NotificationsApi by inject()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val intent by lazy {
        Intent(this, SingleActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(this, 0, intent, 0)
    }

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        scope.safeLaunch(::handleError) {
            notificationsApi.listenToCurrentUserNotifications().collect { result ->
                val unreadNotifications = result.handleResult()?.filter {
                    it.unread
                }

                unreadNotifications?.forEach {
                    showNotification(it)
                    markAsRead(it)
                }
            }
        }
    }

    private suspend fun markAsRead(
        notification: UserNotification
    ) = withContext(Dispatchers.IO) {
        notificationsApi.markNotificationAsRead(notification.id)
    }

    private fun showNotification(notification: UserNotification) {
        // TODO Use real ids
        val builder = NotificationCompat.Builder(this, "buddies")
            .setSmallIcon(R.drawable.ic_baseline_pets)
            .setContentTitle(getString(notification.type.description))
            .setContentText(getString(R.string.invite_notification_message,
                notification.userName,
                getString(notification.category.title),
                notification.petInfo.name))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        // TODO Create real channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Buddies"
            val descriptionText = "Test description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("buddies", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager? =
                ContextCompat.getSystemService(this, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
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
