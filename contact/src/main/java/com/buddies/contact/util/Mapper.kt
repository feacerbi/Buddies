package com.buddies.contact.util

import com.buddies.contact.model.ShareInfoField

fun List<ShareInfoField>.toContactInfo() =
    filter { it.checked }
        .map { it.type to it.input }
        .toMap()