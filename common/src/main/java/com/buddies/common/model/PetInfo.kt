package com.buddies.common.model

import java.util.*

data class PetInfo(
    val tag: String = "",
    val name: String = "",
    val photo: String = "",
    val animal: String = "",
    val breed: String = "",
    val lost: Boolean = false,
    val created: Long = Calendar.getInstance().timeInMillis
)