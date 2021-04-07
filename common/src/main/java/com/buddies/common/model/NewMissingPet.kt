package com.buddies.common.model

import android.net.Uri

data class NewMissingPet(
    val name: String = "",
    val photo: Uri = Uri.EMPTY,
    val animal: Animal? = null,
    val breed: Breed? = null,
    val contactInfo: Map<InfoType, String>
)