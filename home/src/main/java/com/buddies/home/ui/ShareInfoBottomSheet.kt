package com.buddies.home.ui

import android.text.InputType
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.buddies.common.model.ShareInfoType.EMAIL
import com.buddies.common.model.ShareInfoType.LOCATION
import com.buddies.common.model.ShareInfoType.NAME
import com.buddies.common.model.ShareInfoType.PHONE
import com.buddies.common.ui.bottomsheet.BottomSheetFactory
import com.buddies.home.R
import com.buddies.home.databinding.ShareInfoLayoutBinding
import com.buddies.home.model.ShareInfo
import com.buddies.home.model.ShareInfoField
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
            checked: Boolean = true,
            hint: Int = R.string.name_hint,
            validCheck: (ShareInfoField) -> Boolean = defaultValidationCheck
        ): Builder {
            fields.add(ShareInfoField(
                NAME,
                hint,
                checked,
                name,
                InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                R.drawable.ic_baseline_person,
                validCheck))
            return this
        }

        fun email(
            email: String = "",
            checked: Boolean = true,
            hint: Int = R.string.email_hint,
            validCheck: (ShareInfoField) -> Boolean = defaultValidationCheck
        ): Builder {
            fields.add(ShareInfoField(
                EMAIL,
                hint, checked,
                email,
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                R.drawable.ic_baseline_email,
                validCheck))
            return this
        }

        fun phone(
            phone: String = "",
            checked: Boolean = true,
            hint: Int = R.string.phone_hint,
            validCheck: (ShareInfoField) -> Boolean = defaultValidationCheck
        ): Builder {
            fields.add(ShareInfoField(
                PHONE,
                hint,
                checked,
                phone,
                InputType.TYPE_CLASS_PHONE,
                R.drawable.ic_baseline_phone,
                validCheck))
            return this
        }

        fun location(
            location: String = "",
            checked: Boolean = true,
            hint: Int = R.string.location_hint,
            validCheck: (ShareInfoField) -> Boolean = defaultValidationCheck
        ): Builder {
            fields.add(ShareInfoField(
                LOCATION,
                hint,
                checked,
                location,
                InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS,
                R.drawable.ic_baseline_location_on,
                validCheck))
            return this
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
            val info = listOf(*adapter.currentList().toTypedArray())

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
            adapter.updateItems(fields)
            adapter.onCheckedChanged = ::checkButtonConditions
            shareView.fieldsList.adapter = adapter
            return ShareInfoBottomSheet(bottomSheet)
        }
    }
}