package com.buddies.common.model

import android.net.Uri
import com.buddies.common.model.MissingType.LOST

data class NewMissingPet(
    var name: String? = null,
    var type: MissingType = LOST,
    var description: String = "",
    var photo: Uri? = null,
    var animal: Animal? = null,
    var breed: Breed? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var contactInfo: Map<InfoType, String>? = null
)