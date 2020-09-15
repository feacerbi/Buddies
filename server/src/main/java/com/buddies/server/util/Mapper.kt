package com.buddies.server.util

import com.buddies.common.model.InviteNotification
import com.buddies.common.model.Pet
import com.buddies.common.model.PetFoundNotification
import com.buddies.common.model.User
import com.buddies.common.util.toOwnershipCategory
import com.buddies.server.model.Notification

fun Notification.toInviteNotification(
    inviter: User,
    pet: Pet
): InviteNotification = InviteNotification(
    id,
    info.unread,
    info.timestamp.toDate(),
    inviter.info.name,
    pet,
    info.category.toOwnershipCategory()
)

fun Notification.toPetFoundNotification(
    sourceUser: User,
    pet: Pet
): PetFoundNotification = PetFoundNotification(
    id,
    info.unread,
    info.timestamp.toDate(),
    sourceUser.info.name,
    pet
)