package com.buddies.contact.ui.adapter

import android.location.Location
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buddies.common.model.InfoType
import com.buddies.common.util.LocationConverter
import com.buddies.common.util.inflater
import com.buddies.common.util.isNotNullNorBlank
import com.buddies.common.util.safeLaunch
import com.buddies.contact.R
import com.buddies.contact.databinding.LocationInfoItemBinding
import com.buddies.contact.databinding.ShareInfoItemBinding
import com.buddies.contact.model.ShareInfoField
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.CoroutineScope
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class ShareInfoAdapter(
    private val coroutineScope: CoroutineScope? = null,
    private val locationConverter: LocationConverter? = null,
    private val onCurrentLocationRequested: () -> Unit = {},
    private val onFieldCheckedChanged: (List<ShareInfoField>) -> Unit = { _ ->}
) : ListAdapter<ShareInfoField, RecyclerView.ViewHolder>(ShareInfoDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            LOCATION_INFO_VIEW_TYPE -> LocationViewHolder(
                LocationInfoItemBinding.inflate(parent.inflater(), parent, false)
            )
            else -> ShareInfoViewHolder(
                ShareInfoItemBinding.inflate(parent.inflater(), parent, false)
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LocationViewHolder -> holder.bind(getItem(position))
            is ShareInfoViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position).type) {
            InfoType.LOCATION -> LOCATION_INFO_VIEW_TYPE
            else -> INFO_VIEW_TYPE
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
            input.setSelection(item.input.length)
            input.inputType = item.inputType

            inputLayout.hint = binding.root.context.resources.getString(item.hint)
            inputLayout.error = item.error

            icon.setImageResource(item.icon)
            icon.isEnabled = item.checked

            checkbox.setOnCheckedChangeListener { _, checked ->
                val currentItem = getItem(bindingAdapterPosition)
                currentItem.checked = checked
                validateChange(root, currentItem)
                onFieldCheckedChanged.invoke(currentList)
            }

            input.tag = input.doAfterTextChanged {
                val currentItem = getItem(bindingAdapterPosition)
                currentItem.input = it.toString()
                validateChange(root, currentItem)
            }
        }
    }

    inner class LocationViewHolder(
        private val binding: LocationInfoItemBinding
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
            input.setSelection(item.input.length)
            input.inputType = item.inputType
            input.hint = binding.root.context.resources.getString(item.hint)

            inputLayout.error = item.error

            actionButton.setOnClickListener {
                onCurrentLocationRequested.invoke()
            }

            icon.setImageResource(item.icon)
            icon.isEnabled = item.checked

            checkbox.setOnCheckedChangeListener { _, checked ->
                val currentItem = getItem(bindingAdapterPosition)
                currentItem.checked = checked
                validateChange(root, currentItem)
                onFieldCheckedChanged.invoke(currentList)
            }

            input.tag = input.doAfterTextChanged {
                val currentItem = getItem(bindingAdapterPosition)
                currentItem.input = it.toString()
                validateChange(root, currentItem)
                createAutocompleteSuggestions(input, currentItem, it.toString())
            }
        }
    }

    fun createInfoSummary() = currentList
        .filter { it.checked }
        .map { it.toShareInfo() }

    fun checkFieldsValid() = currentList
        .filter { it.checked }
        .all { it.error == null }
        .also { validated ->
            if (!validated) { notifyDataSetChanged() }
        }

    fun setCurrentLocationField(
        location: Location?
    ) = coroutineScope?.safeLaunch {
        val address = location?.let {
            locationConverter?.getAddressFromPosition(it.latitude, it.longitude)
        }
        if (address.isNotNullNorBlank()) {
            currentList.find { it.type == InfoType.LOCATION }?.apply {
                input = address
                error = null
            }
            val index = currentList.indexOfFirst { it.type == InfoType.LOCATION }
            if (index != -1) notifyItemChanged(index)
        }
    }

    private fun validateChange(root: View, item: ShareInfoField) {
        val errorText = root.resources.getString(R.string.empty_field_error)
        item.error = if (item.validate()) null else errorText
    }

    private fun createAutocompleteSuggestions(
        input: MaterialAutoCompleteTextView,
        item: ShareInfoField,
        query: String
    ) = coroutineScope?.safeLaunch {
        val suggestionsAdapter = ArrayAdapter(
            input.context,
            android.R.layout.simple_list_item_1,
            item.suggestions.invoke(query)
        )
        input.setAdapter(suggestionsAdapter)
        input.showDropDown()
    }

    class ShareInfoDiffUtil : DiffUtil.ItemCallback<ShareInfoField>() {
        override fun areItemsTheSame(oldItem: ShareInfoField, newItem: ShareInfoField) =
            oldItem.type == newItem.type

        override fun areContentsTheSame(oldItem: ShareInfoField, newItem: ShareInfoField) =
            oldItem == newItem
    }

    companion object {
        private const val LOCATION_INFO_VIEW_TYPE = 1
        private const val INFO_VIEW_TYPE = 2
    }
}