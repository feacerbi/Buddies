package com.buddies.generator.model

import android.net.Uri

data class NewTag(
    var value: String = "",
    var encryptedValue: String = "",
    var qrCode: Uri = Uri.EMPTY
)
