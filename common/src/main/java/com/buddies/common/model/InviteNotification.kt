package com.buddies.common.model

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import androidx.core.app.NotificationCompat
import com.buddies.common.R
import com.buddies.common.model.NotificationType.INVITE
import com.buddies.common.ui.activity.SingleActivity
import com.buddies.common.util.customTextAppearance
import java.util.*

data class InviteNotification(
    val notificationId: String,
    val notificationUnread: Boolean,
    val notificationTimestamp: Calendar,
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

    override fun build(
        context: Context
    ): Notification =
        NotificationCompat.Builder(context, INVITATION_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.buddies_logo_white)
            .setContentTitle(context.resources.getString(type.description))
            .setContentText(context.resources.getString(R.string.invite_notification_message,
                userName,
                context.resources.getString(category.title),
                pet.info.name))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createPendingIntent(context))
            .setAutoCancel(true)
            .build()

    private fun createIntent(context: Context) =
        Intent(context, SingleActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

    private fun createPendingIntent(context: Context) =
        PendingIntent.getActivity(context, 0, createIntent(context), 0)
}