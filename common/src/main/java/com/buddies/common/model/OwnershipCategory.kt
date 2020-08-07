package com.buddies.common.model

import androidx.annotation.StringRes
import com.buddies.common.R
import com.buddies.common.model.OwnershipAccess.EDIT_ALL
import com.buddies.common.model.OwnershipAccess.READ_ALL

enum class OwnershipCategory(
    val id: Int,
    val access: OwnershipAccess,
    @StringRes val title: Int,
    @StringRes val description: Int
) {
    OWNER(0, EDIT_ALL, R.string.owner_title, R.string.owner_category_description),
    FAMILY(1, READ_ALL, R.string.family_title, R.string.family_category_description),
    FRIEND(2, READ_ALL, R.string.friend_title, R.string.friend_category_description),
    CARE_TAKER(3, READ_ALL, R.string.caretaker_title, R.string.caretaker_category_description),
    VISITOR(-1, READ_ALL, R.string.visitor_title, R.string.visitor_category_description);

    companion object {
        fun getCategoriesList() = listOf(
            OWNER,
            FAMILY,
            FRIEND,
            CARE_TAKER,
            VISITOR
        )
    }
}