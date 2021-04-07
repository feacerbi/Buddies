package com.buddies.contact.ui.bottomsheet

import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.buddies.common.model.InfoType.EMAIL
import com.buddies.common.model.InfoType.LOCATION
import com.buddies.common.model.InfoType.NAME
import com.buddies.common.model.InfoType.PHONE
import com.buddies.common.ui.bottomsheet.BottomSheetFactory
import com.buddies.contact.R
import com.buddies.contact.databinding.ContactInfoLayoutBinding
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

        private val fields: MutableList<com.buddies.contact.model.ContactInfoField> = mutableListOf()

        private val adapter = com.buddies.contact.ui.adapter.ContactInfoAdapter()

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
            contactInfo: com.buddies.contact.model.ContactInfo
        ): Builder {
            when (contactInfo.infoType) {
                NAME -> fields.add(com.buddies.contact.model.NameInfoField(contactInfo.info))
                EMAIL -> fields.add(com.buddies.contact.model.EmailInfoField(contactInfo.info))
                PHONE -> fields.add(com.buddies.contact.model.PhoneInfoField(contactInfo.info))
                LOCATION -> fields.add(com.buddies.contact.model.MapInfoField(contactInfo.info))
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