package com.buddies.common.model

data class UserNotification(
    val id: String,
    val userName: String,
    val petInfo: PetInfo,
    val category: OwnershipCategory,
    val type: NotificationType
)