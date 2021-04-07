package com.buddies.contact.ui.bottomsheet

import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.buddies.common.ui.bottomsheet.BottomSheetFactory
import com.buddies.contact.R
import com.buddies.contact.databinding.ShareInfoLayoutBinding
import com.buddies.contact.model.ShareInfo
import com.buddies.contact.model.ShareInfoField
import com.buddies.contact.ui.adapter.ShareInfoAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class ShareInfoBottomSheet private constructor(
    private val bottomSheet: BottomSheetDialog
) {

    fun show() {
        bottomSheet.show()
    }

    class Builder(
        inflater: LayoutInflater
    ) : BottomSheetFactory() {

        private val shareView = ShareInfoLayoutBinding.inflate(inflater)
        private val bottomSheet = createBottomSheet(shareView.root)

        private val fields: MutableList<ShareInfoField> = mutableListOf()

        private val adapter = ShareInfoAdapter()

        private val confirmButtonDefaultText by lazy {
            shareView.root.context.resources.getString(R.string.send_button)
        }
        private val cancelButtonDefaultText by lazy {
            shareView.root.context.resources.getString(R.string.cancel_button)
        }
        private val emptyErrorText by lazy {
            shareView.root.context.resources.getString(R.string.empty_field_error)
        }

        private val defaultValidationCheck: (ShareInfoField) -> Boolean = {
            it.checked.not() || it.input.isNotBlank()
        }

        fun title(title: String) = with (shareView) {
            dialogTitle.text = title
        }.let { this }

        fun content(content: String) = with (shareView) {
            dialogContent.text = content
        }.let { this }

        fun name(
            name: String = "",
            hint: Int = R.string.name_hint,
            checked: Boolean = true,
            validCheck: (ShareInfoField) -> Boolean = defaultValidationCheck
        ): Builder = apply {
            fields.add(ShareInfoField.createNameField(name, hint, checked, validCheck))
        }

        fun email(
            email: String = "",
            hint: Int = R.string.email_hint,
            checked: Boolean = true,
            validCheck: (ShareInfoField) -> Boolean = defaultValidationCheck
        ): Builder = apply {
            fields.add(ShareInfoField.createEmailField(email, hint, checked, validCheck))
        }

        fun phone(
            phone: String = "",
            hint: Int = R.string.phone_hint,
            checked: Boolean = true,
            validCheck: (ShareInfoField) -> Boolean = defaultValidationCheck
        ): Builder = apply {
            fields.add(ShareInfoField.cratePhoneField(phone, hint, checked, validCheck))
        }

        fun location(
            location: String = "",
            hint: Int = R.string.location_hint,
            checked: Boolean = true,
            validCheck: (ShareInfoField) -> Boolean = defaultValidationCheck
        ): Builder = apply {
            fields.add(ShareInfoField.createLocationField(location, hint, checked, validCheck))
        }

        fun cancelButton(
            title: String = cancelButtonDefaultText,
            action: () -> Unit = {}
        ) = with (shareView) {
            cancelButton.isVisible = true
            cancelButton.text = title
            cancelButton.setOnClickListener {
                action.invoke()
                bottomSheet.cancel()
            }
        }.let { this }

        fun confirmButton(
            title: String = confirmButtonDefaultText,
            action: (List<ShareInfo>) -> Unit = {}
        ) = with (shareView) {
            confirmButton.text = title
            confirmButton.setOnClickListener {
                if (checkSendConditions()) {
                    action.invoke(adapter.createInfoSummary())
                    bottomSheet.cancel()
                }
            }
        }.let { this }

        private fun checkButtonConditions(fields: List<ShareInfoField>) {
            shareView.confirmButton.isEnabled = fields.any { it.checked }
        }

        private fun checkSendConditions(): Boolean {
            val info = adapter.currentList

            val infoValid = info
                .filter { it.checked }
                .all { it.input.isNotBlank() }

            if (infoValid.not()) {
                runValidationOnEveryField(info)
                adapter.notifyDataSetChanged()
            }

            return infoValid
        }

        private fun runValidationOnEveryField(info: List<ShareInfoField>) {
            info.forEach {
                it.error = if (it.validate()) null else emptyErrorText
            }
        }

        fun build(): ShareInfoBottomSheet {
            adapter.submitList(fields)
            adapter.onCheckedChanged = ::checkButtonConditions
            shareView.fieldsList.adapter = adapter
            return ShareInfoBottomSheet(bottomSheet)
        }
    }
}