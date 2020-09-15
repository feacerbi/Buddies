package com.buddies.common.model

import android.content.Context
import android.text.SpannableString
import java.util.*

abstract class UserNotification(
    val id: String,
    val type: NotificationType,
    val unread: Boolean,
    val timestamp: Date
) {
    abstract fun getMessage(context: Context): SpannableString
}