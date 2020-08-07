package com.buddies.profile.ui

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.buddies.common.model.NotificationType.INVITE
import com.buddies.common.model.UserNotification
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.inflater
import com.buddies.mypets.R
import com.buddies.profile.databinding.InviteNotificationItemBinding
import com.buddies.profile.ui.NotificationsAdapter.NotificationViewHolder

class NotificationsAdapter(
    items: List<UserNotification>? = null,
    var owner: LifecycleOwner? = null,
    var ignoreAction: (UserNotification) -> Unit,
    var acceptAction: (UserNotification) -> Unit
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

            petIcon.load(notification.petInfo.photo) {
                createLoadRequest(owner,  true, R.drawable.ic_baseline_pets)
            }

            message.text = root.context.resources.getString(
                com.buddies.profile.R.string.invite_notification_message,
                notification.userName,
                root.context.resources.getString(notification.category.title),
                notification.petInfo.name)

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
        }

    }
}