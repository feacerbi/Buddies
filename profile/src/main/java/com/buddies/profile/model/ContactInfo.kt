package com.buddies.profile.model

import com.buddies.common.model.ShareInfoType

data class ContactInfo(
    val shareInfoType: ShareInfoType,
    val info: String
)
