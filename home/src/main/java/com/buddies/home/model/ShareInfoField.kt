package com.buddies.home.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ShareInfoField(
    val type: ShareInfoType,
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
}
