package com.buddies.common.model

import java.util.*

data class MissingPetInfo(
    val name: String = "",
    val photo: String = "",
    val animal: String = "",
    val breed: String = "",
    val reporter: String = "",
    val created: Long = Calendar.getInstance().timeInMillis
)
