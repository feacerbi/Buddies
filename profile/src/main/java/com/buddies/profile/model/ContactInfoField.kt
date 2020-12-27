package com.buddies.profile.model

import android.content.Context
import androidx.annotation.DrawableRes
import com.buddies.common.model.ShareInfoType

abstract class ContactInfoField(
    val shareInfoType: ShareInfoType,
    val priority: Int,
    val title: String,
    @DrawableRes val icon: Int,
    @DrawableRes val actionIcon: Int = 0,
    val action: (Context) -> Unit = {}
)
