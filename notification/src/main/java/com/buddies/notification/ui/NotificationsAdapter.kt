package com.buddies.notification.ui

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.buddies.common.model.InviteNotification
import com.buddies.common.model.NotificationType.INVITE
import com.buddies.common.model.NotificationType.PET_FOUND
import com.buddies.common.model.PetFoundNotification
import com.buddies.common.model.UserNotification
import com.buddies.common.util.createLoadRequest
import com.buddies.common.util.inflater
import com.buddies.common.util.toFormatted
import com.buddies.notification.R
import com.buddies.notification.databinding.InviteNotificationItemBinding
import com.buddies.notification.databinding.PetFoundNotificationItemBinding

class NotificationsAdapter(
    items: List<UserNotification>? = null,
    var owner: LifecycleOwner? = null,
    var ignoreAction: (UserNotification) -> Unit,
    var acceptAction: (UserNotification) -> Unit,
    var iconClickAction: (UserNotification) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val notifications = mutableListOf<UserNotification>()

    init {
        if (items != null) notifications.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            INVITE.id -> InviteNotificationViewHolder(
                InviteNotificationItemBinding.inflate(parent.inflater(), parent, false))
            PET_FOUND.id -> PetFoundNotificationViewHolder(
                PetFoundNotificationItemBinding.inflate(parent.inflater(), parent, false))
            else -> throw IllegalArgumentException("Unknown notification type")
        }

    override fun getItemCount(): Int = notifications.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val notification = notifications[position]) {
            is InviteNotification -> if (holder is InviteNotificationViewHolder) holder.bind(notification)
            is PetFoundNotification -> if (holder is PetFoundNotificationViewHolder) holder.bind(notification)
        }
    }

    override fun getItemViewType(position: Int): Int = notifications[position].type.id

    inner class InviteNotificationViewHolder(
        private val binding: InviteNotificationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            notification: InviteNotification
        ) = with (binding) {

            petIcon.setOnClickListener { iconClickAction.invoke(notification) }
            petIcon.load(notification.pet.info.photo) {
                createLoadRequest(owner,  true, R.drawable.ic_baseline_pets)
            }

            message.text = notification.getMessage(root.context)

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

    inner class PetFoundNotificationViewHolder(
        private val binding: PetFoundNotificationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            notification: PetFoundNotification
        ) = with (binding) {

            petIcon.setOnClickListener { iconClickAction.invoke(notification) }
            petIcon.load(notification.pet.info.photo) {
                createLoadRequest(owner,  true, R.drawable.ic_baseline_pets)
            }

            message.text = notification.getMessage(root.context)

            timestamp.text = notification.timestamp.toFormatted(binding.root.context)
        }
    }
}