package com.buddies.contact.ui.adapter

import android.text.TextWatcher
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.util.inflater
import com.buddies.contact.R
import com.buddies.contact.databinding.ShareInfoItemBinding
//import com.buddies.contact.databinding.ShareInfoItemBinding
import com.buddies.contact.model.ShareInfoField

class ShareInfoAdapter(
    var onFieldChanged: (List<ShareInfoField>, Boolean) -> Unit = { _, _ ->}
) : ListAdapter<ShareInfoField, ShareInfoAdapter.ShareInfoViewHolder>(ShareInfoDiffUtil()) {

    override fun onCurrentListChanged(
        previousList: MutableList<ShareInfoField>,
        currentList: MutableList<ShareInfoField>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        onFieldChanged.invoke(currentList, checkedFieldsValid())
    }

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
            input.setSelection(input.text?.length ?: 0)
            input.inputType = item.inputType

            inputLayout.isEnabled = item.checked
            inputLayout.hint = binding.root.context.resources.getString(item.hint)
            inputLayout.error = item.error

            icon.setImageResource(item.icon)
            icon.isEnabled = item.checked

            checkbox.setOnCheckedChangeListener { _, checked ->
                val errorText = root.resources.getString(R.string.empty_field_error)
                item.checked = checked
                item.error = if (item.validate()) null else errorText

                notifyItemChanged(bindingAdapterPosition)
                onFieldChanged.invoke(currentList, checkedFieldsValid())
            }

            input.tag = input.doAfterTextChanged {
                val errorText = root.resources.getString(R.string.empty_field_error)
                item.input = input.text.toString()
                item.error = if (item.validate()) null else errorText

                notifyItemChanged(bindingAdapterPosition)
                onFieldChanged.invoke(currentList, checkedFieldsValid())
            }
        }
    }

    fun createInfoSummary() = currentList
        .filter { it.checked }
        .map { it.toShareInfo() }

    private fun checkedFieldsValid() = currentList
        .filter { it.checked }
        .all { it.error == null }

    class ShareInfoDiffUtil : DiffUtil.ItemCallback<ShareInfoField>() {
        override fun areItemsTheSame(oldItem: ShareInfoField, newItem: ShareInfoField) =
            oldItem.type == newItem.type

        override fun areContentsTheSame(oldItem: ShareInfoField, newItem: ShareInfoField) =
            oldItem == newItem
    }
}