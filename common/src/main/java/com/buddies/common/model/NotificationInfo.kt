package com.buddies.common.model

data class NotificationInfo(
    val type: Int = -1,
    val userId: String = "",
    val inviterId: String = "",
    val petId: String = "",
    val category: Int = -1
)