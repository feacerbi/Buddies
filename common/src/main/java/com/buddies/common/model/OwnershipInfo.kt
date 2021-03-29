package com.buddies.common.model

import java.util.*

data class OwnershipInfo(
    val petId: String = "",
    val userId: String = "",
    val category: Int = -1,
    val created: Long = Calendar.getInstance().timeInMillis
)