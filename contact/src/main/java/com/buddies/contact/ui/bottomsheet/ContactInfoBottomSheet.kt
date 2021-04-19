package com.buddies.contact.ui.bottomsheet

import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.view.isVisible
import com.buddies.common.model.InfoType.EMAIL
import com.buddies.common.model.InfoType.LOCATION
import com.buddies.common.model.InfoType.NAME
import com.buddies.common.model.InfoType.PHONE
import com.buddies.common.ui.bottomsheet.BottomSheetFactory
import com.buddies.contact.R
import com.buddies.contact.databinding.ContactInfoLayoutBinding
import com.buddies.contact.model.ContactInfo
import com.buddies.contact.model.ContactInfoField
import com.buddies.contact.model.EmailInfoField
import com.buddies.contact.model.MapInfoField
import com.buddies.contact.model.NameInfoField
import com.buddies.contact.model.PhoneInfoField
import com.buddies.contact.ui.adapter.ContactInfoAdapter
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

        private val adapter = ContactInfoAdapter(contactView.root.context) {
            Toast.makeText(
                contactView.root.context,
                contactView.root.context.getString(R.string.copy_to_clipboard_toast),
                Toast.LENGTH_SHORT
            ).show()
        }

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
            when (contactInfo.infoType) {
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