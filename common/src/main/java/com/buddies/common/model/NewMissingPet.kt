package com.buddies.common.model

import android.net.Uri

data class NewMissingPet(
    var name: String? = null,
    var photo: Uri? = null,
    var animal: Animal? = null,
    var breed: Breed? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var returned: Boolean = false,
    var contactInfo: Map<InfoType, String>? = null
)