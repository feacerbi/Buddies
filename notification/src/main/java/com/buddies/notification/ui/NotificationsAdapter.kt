package com.buddies.notification.ui

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.buddies.common.model.NotificationType.INVITE
import com.buddies.common.model.Pet
import com.buddies.common.model.UserNotification
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.customTextAppearance
import com.buddies.common.util.inflater
import com.buddies.common.util.toFormatted
import com.buddies.notification.R
import com.buddies.notification.databinding.InviteNotificationItemBinding
import com.buddies.notification.ui.NotificationsAdapter.NotificationViewHolder

class NotificationsAdapter(
    items: List<UserNotification>? = null,
    var owner: LifecycleOwner? = null,
    var ignoreAction: (UserNotification) -> Unit,
    var acceptAction: (UserNotification) -> Unit,
    var clickAction: (Pet) -> Unit
) : RecyclerView.Adapter<NotificationViewHolder>() {

    private val notifications = mutableListOf<UserNotification>()

    init {
        if (items != null) notifications.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(
            when (viewType) {
                INVITE.id -> InviteNotificationItemBinding.inflate(parent.inflater(), parent, false)
                else -> throw IllegalArgumentException("Unknown notification type")
            }
        )

    override fun getItemCount(): Int = notifications.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemViewType(position: Int): Int = notifications[position].type.id

    inner class NotificationViewHolder(
        private val binding: InviteNotificationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            notification: UserNotification
        ) = with (binding) {

            petIcon.load(notification.pet.info.photo) {
                createLoadRequest(owner,  true, R.drawable.ic_baseline_pets)
            }
            petIcon.setOnClickListener {
                clickAction.invoke(notification.pet)
            }

            val inviterName = notification.userName
            val petName = notification.pet.info.name
            val category = root.context.resources.getString(notification.category.title)

            message.text = root.context.resources.getString(
                R.string.invite_notification_message,
                inviterName,
                category,
                petName
            ).customTextAppearance(binding.root.context,
                arrayOf(category, petName),
                R.style.NotificationMessage_Highlight
            )

            ignoreButton.setOnClickListener {
                acceptButton.isEnabled = false
                ignoreButton.isEnabled = false
                ignoreAction.invoke(notification)
            }
            acceptButton.setOnClickListener {
                acceptButton.isEnabled = false
                ignoreButton.isEnabled = false
                acceptAction.invoke(notification)
            }

            timestamp.text = notification.timestamp.toFormatted(binding.root.context)
        }

    }
}