package com.buddies.contact.model

import android.content.Context
import androidx.annotation.DrawableRes
import com.buddies.common.model.InfoType

abstract class ContactInfoField(
    val infoType: InfoType,
    val priority: Int,
    val title: String,
    @DrawableRes val icon: Int,
    @DrawableRes val actionIcon: Int = 0,
    val action: (Context) -> Unit = {}
)
