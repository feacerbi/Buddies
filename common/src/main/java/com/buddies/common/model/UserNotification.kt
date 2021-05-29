package com.buddies.common.model

import android.app.Activity
import android.app.Notification
import android.content.Context
import android.text.SpannableString
import java.util.*

abstract class UserNotification(
    val id: String,
    val type: NotificationType,
    val unread: Boolean,
    val timestamp: Calendar
) {
    abstract fun getMessage(context: Context): SpannableString
    abstract fun <T : Activity> build(context: Context, targetActivity: Class<T>): Notification
}