package com.buddies.profile.ui.bottomsheet

import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.buddies.common.model.ShareInfoType.EMAIL
import com.buddies.common.model.ShareInfoType.LOCATION
import com.buddies.common.model.ShareInfoType.NAME
import com.buddies.common.model.ShareInfoType.PHONE
import com.buddies.common.ui.bottomsheet.BottomSheetFactory
import com.buddies.profile.R
import com.buddies.profile.databinding.ContactInfoLayoutBinding
import com.buddies.profile.model.ContactInfo
import com.buddies.profile.model.ContactInfoField
import com.buddies.profile.model.EmailInfoField
import com.buddies.profile.model.MapInfoField
import com.buddies.profile.model.NameInfoField
import com.buddies.profile.model.PhoneInfoField
import com.buddies.profile.ui.adapter.ContactInfoAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class ContactInfoBottomSheet private constructor(
    private val bottomSheet: BottomSheetDialog
) {

    fun show() {
        bottomSheet.show()
    }

    class Builder(
        inflater: LayoutInflater
    ) : BottomSheetFactory() {

        private val contactView = ContactInfoLayoutBinding.inflate(inflater)
        private val bottomSheet = createBottomSheet(contactView.root)

        private val fields: MutableList<ContactInfoField> = mutableListOf()

        private val adapter = ContactInfoAdapter()

        private val closeButtonDefaultText by lazy {
            contactView.root.context.resources.getString(R.string.close_button)
        }

        fun title(title: String) = with (contactView) {
            dialogTitle.text = title
        }.let { this }

        fun content(content: String) = with (contactView) {
            dialogContent.text = content
        }.let { this }

        fun field(
            contactInfo: ContactInfo
        ): Builder {
            when (contactInfo.shareInfoType) {
                NAME -> fields.add(NameInfoField(contactInfo.info))
                EMAIL -> fields.add(EmailInfoField(contactInfo.info))
                PHONE -> fields.add(PhoneInfoField(contactInfo.info))
                LOCATION -> fields.add(MapInfoField(contactInfo.info))
                else -> { /* Ignore */ }
            }
            return this
        }

        fun closeButton(
            title: String = closeButtonDefaultText,
            action: () -> Unit = {}
        ) = with (contactView) {
            closeButton.isVisible = true
            closeButton.text = title
            closeButton.setOnClickListener {
                action.invoke()
                bottomSheet.cancel()
            }
        }.let { this }

        fun build(): ContactInfoBottomSheet {
            adapter.submitList(fields.sortedBy { it.priority })
            contactView.fieldsList.adapter = adapter
            return ContactInfoBottomSheet(bottomSheet)
        }
    }
}