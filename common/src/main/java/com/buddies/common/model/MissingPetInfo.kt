package com.buddies.common.model

import java.util.*

data class MissingPetInfo(
    val name: String = "",
    val photo: String = "",
    val animal: String = "",
    val breed: String = "",
    val reporter: String = "",
    val reporterInfo: Map<String, String> = emptyMap(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val returned: Boolean? = false,
    val created: Long = Calendar.getInstance().timeInMillis
)
