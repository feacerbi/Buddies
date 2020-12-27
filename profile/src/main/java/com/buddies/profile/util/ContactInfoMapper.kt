package com.buddies.profile.util

import com.buddies.common.model.ShareInfoType
import com.buddies.profile.model.ContactInfo

fun Map<ShareInfoType, String>.toContactInfo(): List<ContactInfo> =
    toList().map { ContactInfo(it.first, it.second) }