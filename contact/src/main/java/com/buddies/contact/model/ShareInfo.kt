package com.buddies.contact.model

import com.buddies.common.model.InfoType

data class ShareInfo(
    val type: InfoType,
    val info: String
) {
    fun toPair() = type to info
}
