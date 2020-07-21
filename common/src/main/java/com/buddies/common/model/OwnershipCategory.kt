package com.buddies.common.model

import androidx.annotation.StringRes
import com.buddies.common.R
import com.buddies.common.model.OwnershipAccess.EDIT_ALL
import com.buddies.common.model.OwnershipAccess.READ_ALL

enum class OwnershipCategory(
    val access: OwnershipAccess,
    @StringRes val description: Int
) {
    OWNER(EDIT_ALL, R.string.owner_category_description),
    FAMILY(READ_ALL, R.string.family_category_description),
    FRIEND(READ_ALL, R.string.friend_category_description),
    CARE_TAKER(READ_ALL, R.string.caretaker_category_description),
    VISITOR(READ_ALL, R.string.visitor_category_description);

    fun fromName() = when (name) {
        OWNER.name -> OWNER
        FAMILY.name -> FAMILY
        FRIEND.name -> FRIEND
        CARE_TAKER.name -> CARE_TAKER
        else -> VISITOR
    }

    companion object {
        fun getCategoriesList() = listOf(
            OwnershipCategory.OWNER,
            OwnershipCategory.FAMILY,
            OwnershipCategory.FRIEND,
            OwnershipCategory.CARE_TAKER,
            VISITOR
        )
    }
}