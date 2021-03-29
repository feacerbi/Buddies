package com.buddies.common.model

import java.util.*

data class UserInfo(
    var name: String = "",
    var email: String = "",
    var photo: String = "",
    val created: Long = Calendar.getInstance().timeInMillis
)