package com.buddies.home.ui.adapter

import android.text.TextWatcher
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.util.inflater
import com.buddies.home.databinding.ShareInfoItemBinding
import com.buddies.home.model.ShareInfoField

class ShareInfoAdapter(
    var onCheckedChanged: (List<ShareInfoField>) -> Unit = {}
) : RecyclerView.Adapter<ShareInfoAdapter.ShareInfoViewHolder>() {

    private val fieldsList: MutableList<ShareInfoField> = mutableListOf()

    fun updateItems(items: List<ShareInfoField>) {
        fieldsList.clear()
        fieldsList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareInfoViewHolder =
        ShareInfoViewHolder(
            ShareInfoItemBinding.inflate(parent.inflater(), parent, false)
        )

    override fun onBindViewHolder(holder: ShareInfoViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = fieldsList.size

    inner class ShareInfoViewHolder(
        private val binding: ShareInfoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int
        ) = with (binding) {
            val item = fieldsList[position]

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
                notifyItemChanged(position)
                onCheckedChanged.invoke(fieldsList)
            }

            input.tag = input.doAfterTextChanged {
                item.input = it.toString()

                if (item.error != null) {
                    item.error = null
                    notifyItemChanged(position)
                }
            }
        }
    }

    fun currentList() = fieldsList

    fun createInfoSummary() = fieldsList
        .filter { it.checked }
        .map { it.toShareInfo() }
}