package com.buddies.common.model

import android.net.Uri

data class NewPet(
    var tag: String? = null,
    var name: String? = null,
    var photo: Uri? = null,
    var animal: Animal? = null,
    var breed: Breed? = null,
    var age: Int = 0
)