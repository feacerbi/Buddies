package com.buddies.common.model

import android.net.Uri

data class User(
    val id: String,
    val name: String?,
    val email: String?,
    val photo: Uri?
)