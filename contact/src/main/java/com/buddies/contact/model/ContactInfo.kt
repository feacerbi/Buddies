package com.buddies.contact.model

import com.buddies.common.model.InfoType

data class ContactInfo(
    val infoType: InfoType,
    val info: String
)
