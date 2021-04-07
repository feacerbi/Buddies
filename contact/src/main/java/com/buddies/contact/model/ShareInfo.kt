package com.buddies.contact.model

import com.buddies.common.model.InfoType
import java.util.*

data class ShareInfo(
    val type: InfoType,
    val info: String
) {
    fun toPair() = type.name.toLowerCase(Locale.getDefault()) to info
}
