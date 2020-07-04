package com.buddies.common.model

import com.buddies.common.R
import com.buddies.common.model.OwnershipAccess.EDIT_ALL
import com.buddies.common.model.OwnershipAccess.READ_ALL

enum class OwnershipCategory(
    val access: OwnershipAccess,
    val description: Int
) {
    OWNER(EDIT_ALL, R.string.owner_category_description),
    FAMILY(READ_ALL, R.string.family_category_description),
    FRIEND(READ_ALL, R.string.friend_category_description),
    CARE_TAKER(READ_ALL, R.string.caretaker_category_description)
}