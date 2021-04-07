package com.buddies.contact.model

import android.text.InputType
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.buddies.common.model.InfoType
import com.buddies.contact.R

data class ShareInfoField(
    val type: InfoType,
    @StringRes val hint: Int,
    var checked: Boolean,
    var input: String,
    val inputType: Int,
    @DrawableRes val icon: Int,
    val validation: ShareInfoField.() -> Boolean,
    var error: String? = "",
) {
    fun validate() = validation.invoke(this)

    fun toShareInfo() = ShareInfo(type, input)

    companion object {

        fun createNameField(
            name: String = "",
            hint: Int = R.string.name_hint,
            checked: Boolean = true,
            validCheck: (ShareInfoField) -> Boolean = { true }
        ) = ShareInfoField(
            InfoType.NAME,
            hint,
            checked,
            name,
            InputType.TYPE_TEXT_FLAG_CAP_WORDS,
            R.drawable.ic_baseline_person,
            validCheck
        )

        fun createEmailField(
            email: String = "",
            hint: Int = R.string.email_hint,
            checked: Boolean = true,
            validCheck: (ShareInfoField) -> Boolean = { true }
        ) = ShareInfoField(
            InfoType.EMAIL,
            hint, checked,
            email,
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            R.drawable.ic_baseline_email,
            validCheck
        )

        fun cratePhoneField(
            phone: String = "",
            hint: Int = R.string.phone_hint,
            checked: Boolean = true,
            validCheck: (ShareInfoField) -> Boolean = { true }
        ) = ShareInfoField(
            InfoType.PHONE,
            hint,
            checked,
            phone,
            InputType.TYPE_CLASS_PHONE,
            R.drawable.ic_baseline_phone,
            validCheck
        )

        fun createLocationField(
            location: String = "",
            hint: Int = R.string.location_hint,
            checked: Boolean = true,
            validCheck: (ShareInfoField) -> Boolean = { true }
        ) = ShareInfoField(
            InfoType.LOCATION,
            hint,
            checked,
            location,
            InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS,
            R.drawable.ic_baseline_location_on,
            validCheck
        )
    }
}
