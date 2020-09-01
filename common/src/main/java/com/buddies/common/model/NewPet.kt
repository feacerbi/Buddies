package com.buddies.common.model

import android.net.Uri

data class NewPet(
    var tag: String = "",
    var name: String = "",
    var photo: Uri = Uri.EMPTY,
    var animal: Animal? = null,
    var breed: Breed? = null,
    var age: Int = 0
)