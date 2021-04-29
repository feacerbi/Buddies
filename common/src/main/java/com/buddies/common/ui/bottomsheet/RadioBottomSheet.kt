package com.buddies.common.ui.bottomsheet

import android.view.LayoutInflater
import com.buddies.common.R
import com.buddies.common.databinding.SelectableListLayoutBinding
import com.buddies.common.ui.adapter.RadioAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class RadioBottomSheet<T> private constructor(
    private val bottomSheet: BottomSheetDialog
) {

    fun show() {
        bottomSheet.show()
    }

    class Builder<T>(
        inflater: LayoutInflater
    ) : BottomSheetFactory() {

        private val selectableView = SelectableListLayoutBinding.inflate(inflater)
        private val bottomSheet = createBottomSheet(selectableView.root)
        private var adapter: RadioAdapter<T>? = null

        private val confirmButtonDefaultText by lazy {
            selectableView.root.context.resources.getString(R.string.ok_button)
        }
        private val cancelButtonDefaultText by lazy {
            selectableView.root.context.resources.getString(R.string.cancel_button)
        }

        fun title(title: String) = with (selectableView) {
            listTitle.text = title
        }.let { this }

        fun items(
            items: List<T>,
            displayText: (T) -> String = { it.toString() },
            selected: T? = null
        ) = with (selectableView) {
            adapter = RadioAdapter(items, displayText, selected)
            list.adapter = adapter
        }.let { this }

        fun cancelButton(
            title: String = cancelButtonDefaultText,
            action: () -> Unit = {}
        ) = with (selectableView) {
            cancelButton.text = title
            cancelButton.setOnClickListener {
                action.invoke()
                bottomSheet.cancel()
            }
        }.let { this }

        fun confirmButton(
            title: String = confirmButtonDefaultText,
            action: (T?) -> Unit = {}
        ) = with (selectableView) {
            changeButton.isEnabled = true
            changeButton.text = title
            changeButton.setOnClickListener {
                action.invoke(adapter?.getSelectedItem())
                bottomSheet.cancel()
            }
        }.let { this }

        fun build() = RadioBottomSheet<T>(bottomSheet)
    }
}