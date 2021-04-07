package com.buddies.contact.ui.adapter

import android.text.TextWatcher
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.InfoType
import com.buddies.common.model.InfoType.UNKNOWN
import com.buddies.common.util.inflater
import com.buddies.contact.databinding.ShareInfoItemBinding
import com.buddies.contact.model.ShareInfoField

class ShareInfoAdapter(
    var onCheckedChanged: (List<ShareInfoField>) -> Unit = {}
) : ListAdapter<ShareInfoField, ShareInfoAdapter.ShareInfoViewHolder>(ShareInfoDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareInfoViewHolder =
        ShareInfoViewHolder(
            ShareInfoItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: ShareInfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShareInfoViewHolder(
        private val binding: ShareInfoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ShareInfoField
        ) = with (binding) {

            checkbox.setOnCheckedChangeListener(null)
            checkbox.isChecked = item.checked

            val tag = input.tag
            if (tag is TextWatcher) {
                input.removeTextChangedListener(tag)
            }

            input.setText(item.input)
            input.inputType = item.inputType

            inputLayout.isEnabled = item.checked
            inputLayout.hint = binding.root.context.resources.getString(item.hint)
            inputLayout.error = item.error

            icon.setImageResource(item.icon)
            icon.isEnabled = item.checked

            checkbox.setOnCheckedChangeListener { _, checked ->
                item.checked = checked
                item.error = null
                notifyItemChanged(bindingAdapterPosition)
                onCheckedChanged.invoke(currentList)
            }

            input.tag = input.doAfterTextChanged {
                item.input = it.toString()

                if (item.error != null) {
                    item.error = null
                    notifyItemChanged(bindingAdapterPosition)
                }
            }
        }
    }

    fun createInfoSummary() = currentList
        .filter { it.checked }
        .map { it.toShareInfo() }

    fun setError(infoType: InfoType, error: String?) {
        submitList(
            currentList.map { field ->
                field.apply {
                    if (infoType == type || infoType == UNKNOWN) this.error = error
                }
            }
        )
    }

    class ShareInfoDiffUtil : DiffUtil.ItemCallback<ShareInfoField>() {
        override fun areItemsTheSame(oldItem: ShareInfoField, newItem: ShareInfoField) =
            oldItem.type == newItem.type

        override fun areContentsTheSame(oldItem: ShareInfoField, newItem: ShareInfoField) =
            oldItem == newItem
    }
}