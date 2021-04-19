package com.buddies.common.model

import java.util.*

data class MissingPetInfo(
    val name: String = "",
    val photo: String = "",
    val animal: String = "",
    val breed: String = "",
    val reporter: String = "",
    val reporterInfo: Map<String, String> = emptyMap(),
    var latitude: Double? = null,
    var longitude: Double? = null,
    val created: Long = Calendar.getInstance().timeInMillis
)
