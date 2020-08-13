package com.buddies.common.model

import java.util.*

data class UserNotification(
    val id: String,
    val userName: String,
    val petInfo: PetInfo,
    val category: OwnershipCategory,
    val type: NotificationType,
    val unread: Boolean,
    val timestamp: Date
)