package com.buddies.common.ui.bottomsheet

import android.view.LayoutInflater
import com.buddies.common.R
import com.buddies.common.databinding.InputTextLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class InputBottomSheet private constructor(
    private val bottomSheet: BottomSheetDialog
) {

    fun show() {
        bottomSheet.show()
    }

    class Builder(
        inflater: LayoutInflater
    ) : BottomSheetFactory() {

        private val inputView = InputTextLayoutBinding.inflate(inflater)
        private val bottomSheet = createBottomSheet(inputView.root)

        private val confirmButtonDefaultText by lazy {
            inputView.root.context.resources.getString(R.string.ok_button)
        }
        private val cancelButtonDefaultText by lazy {
            inputView.root.context.resources.getString(R.string.cancel_button)
        }

        fun hint(title: String) = with (inputView) {
            inputEditText.hint = title
        }.let { this }

        fun content(content: String) = with (inputView) {
            inputEditText.setText(content)
        }.let { this }

        fun inputType(inputType: Int) = with (inputView) {
            inputEditText.inputType = inputType
        }

        fun cancelButton(
            title: String = cancelButtonDefaultText,
            action: () -> Unit = {}
        ) = with (inputView) {
            cancelButton.text = title
            cancelButton.setOnClickListener {
                action.invoke()
                bottomSheet.cancel()
            }
        }.let { this }

        fun confirmButton(
            title: String = confirmButtonDefaultText,
            action: (String) -> Unit = {}
        ) = with (inputView) {
            changeButton.text = title
            changeButton.setOnClickListener {
                action.invoke(inputEditText.text.toString())
                bottomSheet.cancel()
            }
        }.let { this }

        fun build() = InputBottomSheet(bottomSheet)
    }
}