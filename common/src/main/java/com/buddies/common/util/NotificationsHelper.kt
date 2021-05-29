package com.buddies.common.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun createNotificationChannel(
    context: Context,
    id: String,
    name: String,
    descriptionText: String = ""
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager? =
            ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}

fun showNotification(
    context: Context,
    id: Int,
    notification: Notification
) {
    NotificationManagerCompat.from(context)
        .notify(id, notification)
}