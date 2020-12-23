package com.buddies.common.ui.bottomsheet

import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.buddies.common.R
import com.buddies.common.databinding.SelectableListLayoutBinding
import com.buddies.common.ui.adapter.MediaPickerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class MediaPickerBottomSheet private constructor(
    private val bottomSheet: BottomSheetDialog
) {

    fun show() {
        bottomSheet.show()
    }

    class Builder(
        inflater: LayoutInflater
    ) : BottomSheetFactory() {

        private val selectableView = SelectableListLayoutBinding.inflate(inflater)
        private val bottomSheet = createBottomSheet(selectableView.root)

        init {
            with (selectableView) {
                cancelButton.isVisible = false
                changeButton.isVisible = false
                listTitle.isVisible = false
            }
        }

        private val confirmButtonDefaultText by lazy {
            selectableView.root.context.resources.getString(R.string.ok_button)
        }
        private val cancelButtonDefaultText by lazy {
            selectableView.root.context.resources.getString(R.string.cancel_button)
        }

        fun title(title: String) = with (selectableView) {
            listTitle.text = title
        }.let { this }

        fun selected(action: (MediaPickerAdapter.MediaSource) -> Unit) = with (selectableView) {
            list.adapter = MediaPickerAdapter {
                bottomSheet.dismiss()
                action.invoke(it)
            }
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
            action: () -> Unit = {}
        ) = with (selectableView) {
            changeButton.text = title
            changeButton.setOnClickListener {
                action.invoke()
                bottomSheet.cancel()
            }
        }.let { this }

        fun build() = MediaPickerBottomSheet(bottomSheet)
    }
}