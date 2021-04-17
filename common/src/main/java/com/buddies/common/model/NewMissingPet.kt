package com.buddies.common.model

import android.net.Uri

data class NewMissingPet(
    var name: String? = null,
    var photo: Uri? = null,
    var animal: Animal? = null,
    var breed: Breed? = null,
    var contactInfo: Map<InfoType, String>? = null
)