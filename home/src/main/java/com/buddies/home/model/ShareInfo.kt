package com.buddies.home.model

import com.buddies.common.model.ShareInfoType
import java.util.*

data class ShareInfo(
    val type: ShareInfoType,
    val info: String
) {
    fun toPair() = type.name.toLowerCase(Locale.getDefault()) to info
}
