package com.buddies.common.model

import java.util.*

data class TagInfo(
    val value: String = "",
    val encrypted: String = "",
    val available: Boolean = false,
    val created: Long = Calendar.getInstance().timeInMillis
)