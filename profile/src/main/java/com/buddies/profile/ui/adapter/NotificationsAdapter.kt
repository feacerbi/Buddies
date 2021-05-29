package com.buddies.profile.ui.adapter

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode.UNKNOWN_NOTIFICATION_TYPE
import com.buddies.common.model.InviteNotification
import com.buddies.common.model.NotificationType.INVITE
import com.buddies.common.model.NotificationType.PET_FOUND
import com.buddies.common.model.PetFoundNotification
import com.buddies.common.model.UserNotification
import com.buddies.common.util.inflater
import com.buddies.common.util.load
import com.buddies.common.util.toFormatted
import com.buddies.profile.R
import com.buddies.profile.databinding.InviteNotificationItemBinding
import com.buddies.profile.databinding.PetFoundNotificationItemBinding

class NotificationsAdapter(
    val owner: LifecycleOwner,
    val acceptAction: (InviteNotification) -> Unit,
    val infoAction: (PetFoundNotification) -> Unit,
    val dismissAction: (UserNotification) -> Unit,
    val iconClickAction: (UserNotification) -> Unit
) : ListAdapter<UserNotification, RecyclerView.ViewHolder>(NotificationsDiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            INVITE.id -> InviteNotificationViewHolder(
                InviteNotificationItemBinding.inflate(parent.inflater(), parent, false))
            PET_FOUND.id -> PetFoundNotificationViewHolder(
                PetFoundNotificationItemBinding.inflate(parent.inflater(), parent, false))
            else -> throw DefaultErrorException(DefaultError(UNKNOWN_NOTIFICATION_TYPE))
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val notification = getItem(position)) {
            is InviteNotification -> if (holder is InviteNotificationViewHolder) holder.bind(notification)
            is PetFoundNotification -> if (holder is PetFoundNotificationViewHolder) holder.bind(notification)
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.id

    inner class InviteNotificationViewHolder(
        private val binding: InviteNotificationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            notification: InviteNotification
        ) = with (binding) {

            petIcon.setOnClickListener { iconClickAction.invoke(notification) }
            petIcon.load(notification.pet.info.photo, owner) {
                circleTransform = true
                error = R.drawable.ic_baseline_pets
            }

            message.text = notification.getMessage(root.context)

            ignoreButton.setOnClickListener {
                dismissAction.invoke(notification)
            }
            acceptButton.setOnClickListener {
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
            petIcon.load(notification.pet.info.photo, owner) {
                circleTransform = true
                error = R.drawable.ic_baseline_pets
            }

            message.text = notification.getMessage(root.context)

            dismissButton.setOnClickListener {
                dismissAction.invoke(notification)
            }
            contactButton.setOnClickListener {
                infoAction.invoke(notification)
            }

            timestamp.text = notification.timestamp.toFormatted(binding.root.context)
        }
    }

    class NotificationsDiffUtils : DiffUtil.ItemCallback<UserNotification>() {
        override fun areItemsTheSame(
            oldItem: UserNotification,
            newItem: UserNotification
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: UserNotification,
            newItem: UserNotification
        ): Boolean = oldItem.type == newItem.type
            && oldItem.timestamp == newItem.timestamp
            && oldItem.unread == newItem.unread

    }
}