package com.buddies.common.ui.bottomsheet

import android.view.LayoutInflater
import com.buddies.common.R
import com.buddies.common.databinding.SimpleLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class SimpleBottomSheet private constructor(
    private val bottomSheet: BottomSheetDialog
) {

    fun show() {
        bottomSheet.show()
    }

    class Builder(
        inflater: LayoutInflater
    ) : BottomSheetFactory() {

        private val simpleView = SimpleLayoutBinding.inflate(inflater)
        private val bottomSheet = createBottomSheet(simpleView.root)

        private val confirmButtonDefaultText by lazy {
            simpleView.root.context.resources.getString(R.string.ok_button)
        }
        private val cancelButtonDefaultText by lazy {
            simpleView.root.context.resources.getString(R.string.cancel_button)
        }

        fun title(title: String) = with (simpleView) {
            dialogTitle.text = title
        }.let { this }

        fun content(content: String) = with (simpleView) {
            dialogContent.text = content
        }.let { this }

        fun cancelButton(
            title: String = cancelButtonDefaultText,
            action: () -> Unit = {}
        ) = with (simpleView) {
            cancelButton.text = title
            cancelButton.setOnClickListener {
                action.invoke()
                bottomSheet.cancel()
            }
        }.let { this }

        fun confirmButton(
            title: String = confirmButtonDefaultText,
            action: () -> Unit = {}
        ) = with (simpleView) {
            confirmButton.text = title
            confirmButton.setOnClickListener {
                action.invoke()
                bottomSheet.cancel()
            }
        }.let { this }

        fun build() = SimpleBottomSheet(bottomSheet)
    }
}