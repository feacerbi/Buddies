package com.buddies.server.model

import com.google.firebase.Timestamp

data class NotificationInfo(
    val type: Int = -1,
    val userId: String = "",
    val inviterId: String = "",
    val petId: String = "",
    val category: Int = -1,
    val unread: Boolean = true,
    val timestamp: Timestamp = Timestamp.now()
)