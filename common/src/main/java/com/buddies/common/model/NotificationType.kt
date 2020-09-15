package com.buddies.common.model

import com.buddies.common.R

enum class NotificationType(
    val id: Int,
    val description: Int
) {
    INVITE(0, R.string.invitation_description),
    PET_FOUND(1, R.string.pet_found_description),
    UNKNOWN(-1, R.string.unknown_description)
}