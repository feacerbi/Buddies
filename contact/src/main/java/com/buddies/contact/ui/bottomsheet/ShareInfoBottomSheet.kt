package com.buddies.contact.ui.bottomsheet

import android.location.Location
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.buddies.common.ui.bottomsheet.BottomSheetFactory
import com.buddies.common.util.LocationConverter
import com.buddies.contact.R
import com.buddies.contact.databinding.ShareInfoLayoutBinding
import com.buddies.contact.model.ShareInfo
import com.buddies.contact.model.ShareInfoField
import com.buddies.contact.ui.adapter.ShareInfoAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class ShareInfoBottomSheet private constructor(
    private val shareInfoAdapter: ShareInfoAdapter,
    private val bottomSheet: BottomSheetDialog
) {

    fun setCurrentLocation(location: Location?) {
        shareInfoAdapter.setCurrentLocationField(location)
    }

    fun show() {
        bottomSheet.show()
    }

    @ExperimentalContracts
    class Builder(
        inflater: LayoutInflater
    ) : BottomSheetFactory() {

        private val shareView = ShareInfoLayoutBinding.inflate(inflater)
        private val bottomSheet = createBottomSheet(shareView.root)

        private val fields: MutableList<ShareInfoField> = mutableListOf()

        private var adapter = ShareInfoAdapter { fields ->
            shareView.confirmButton.isEnabled = fields.any { it.checked }
        }

        private val confirmButtonDefaultText by lazy {
            shareView.root.context.resources.getString(R.string.send_button)
        }
        private val cancelButtonDefaultText by lazy {
            shareView.root.context.resources.getString(R.string.cancel_button)
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
            coroutineScope: CoroutineScope,
            locationConverter: LocationConverter,
            currentLocationRequest: () -> Unit,
            validCheck: (ShareInfoField) -> Boolean = defaultValidationCheck
        ): Builder = apply {
            adapter = ShareInfoAdapter(coroutineScope, locationConverter, currentLocationRequest) { fields ->
                shareView.confirmButton.isEnabled = fields.any { it.checked }
            }
            fields.add(ShareInfoField.createLocationField(location, hint, checked, locationConverter, validCheck))
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
                if (adapter.checkFieldsValid()) {
                    action.invoke(adapter.createInfoSummary())
                    bottomSheet.cancel()
                }
            }
        }.let { this }

        fun build(): ShareInfoBottomSheet {
            adapter.submitList(fields)
            shareView.fieldsList.adapter = adapter
            return ShareInfoBottomSheet(adapter, bottomSheet)
        }
    }
}