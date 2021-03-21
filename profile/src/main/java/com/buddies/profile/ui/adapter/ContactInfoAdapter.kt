package com.buddies.profile.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.util.inflater
import com.buddies.profile.databinding.ContactInfoItemBinding
import com.buddies.profile.model.ContactInfoField
import com.buddies.profile.ui.adapter.ContactInfoAdapter.ContactInfoViewHolder

class ContactInfoAdapter : ListAdapter<ContactInfoField, ContactInfoViewHolder>(ContactInfoDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactInfoViewHolder =
        ContactInfoViewHolder(
            ContactInfoItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: ContactInfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ContactInfoViewHolder(
        private val binding: ContactInfoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ContactInfoField
        ) = with (binding) {

            icon.setImageResource(item.icon)

            contactTitle.text = item.title

            action.setImageResource(item.actionIcon)
            action.setOnClickListener {
                item.action.invoke(root.context)
            }
        }
    }

    class ContactInfoDiffUtil : DiffUtil.ItemCallback<ContactInfoField>() {
        override fun areItemsTheSame(oldItem: ContactInfoField, newItem: ContactInfoField) =
            oldItem.shareInfoType == newItem.shareInfoType

        override fun areContentsTheSame(oldItem: ContactInfoField, newItem: ContactInfoField) =
            oldItem.title == newItem.title
    }
}