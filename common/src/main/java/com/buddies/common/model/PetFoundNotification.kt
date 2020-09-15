package com.buddies.common.model

import android.content.Context
import android.text.SpannableString
import com.buddies.common.R
import com.buddies.common.model.NotificationType.PET_FOUND
import com.buddies.common.util.customTextAppearance
import java.util.*

data class PetFoundNotification(
    val notificationId: String,
    val notificationUnread: Boolean,
    val notificationTimestamp: Date,
    val userName: String,
    val pet: Pet,
) : UserNotification(
    notificationId,
    PET_FOUND,
    notificationUnread,
    notificationTimestamp
) {
    override fun getMessage(context: Context): SpannableString =
        context.resources.getString(
            R.string.pet_found_notification_message,
            pet.info.name,
            userName
        ).customTextAppearance(context,
            arrayOf(pet.info.name),
            R.style.NotificationMessage_Highlight
        )
}