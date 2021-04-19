package com.buddies.contact.ui.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.util.inflater
import com.buddies.contact.databinding.ContactInfoItemBinding
import com.buddies.contact.model.ContactInfoField
import com.buddies.contact.ui.adapter.ContactInfoAdapter.ContactInfoViewHolder

class ContactInfoAdapter(
    context: Context,
    val onCopyToClipboard: () -> Unit = {}
) : ListAdapter<ContactInfoField, ContactInfoViewHolder>(ContactInfoDiffUtil()) {

    private val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

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

            copy.setOnClickListener {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(item.infoType.name, item.title))
                onCopyToClipboard.invoke()
            }

            action.setImageResource(item.actionIcon)
            action.setOnClickListener {
                item.action.invoke(root.context)
            }
        }
    }

    class ContactInfoDiffUtil : DiffUtil.ItemCallback<ContactInfoField>() {
        override fun areItemsTheSame(oldItem: ContactInfoField, newItem: ContactInfoField) =
            oldItem.infoType == newItem.infoType

        override fun areContentsTheSame(oldItem: ContactInfoField, newItem: ContactInfoField) =
            oldItem.title == newItem.title
    }
}