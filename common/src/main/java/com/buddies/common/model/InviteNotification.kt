package com.buddies.common.model

import android.content.Context
import android.text.SpannableString
import com.buddies.common.R
import com.buddies.common.model.NotificationType.INVITE
import com.buddies.common.util.customTextAppearance
import java.util.*

data class InviteNotification(
    val notificationId: String,
    val notificationUnread: Boolean,
    val notificationTimestamp: Date,
    val userName: String,
    val pet: Pet,
    val category: OwnershipCategory,
) : UserNotification(
    notificationId,
    INVITE,
    notificationUnread,
    notificationTimestamp
) {
    override fun getMessage(context: Context): SpannableString =
        context.resources.getString(
            R.string.invite_notification_message,
            userName,
            context.resources.getString(category.title),
            pet.info.name
        ).customTextAppearance(context,
            arrayOf(context.resources.getString(category.title), pet.info.name),
            R.style.NotificationMessage_Highlight
        )
}