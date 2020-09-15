package com.buddies.common.util

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode
import com.buddies.common.model.NotificationType.*
import com.buddies.common.model.OwnershipCategory.*

fun Exception.toDefaultError() =
    when (this) {
        is DefaultErrorException -> error
        else -> DefaultError(ErrorCode.UNKNOWN)
    }

fun DefaultError.toException() =
    DefaultErrorException(this)

fun Int.toOwnershipCategory() = when (this) {
    OWNER.id -> OWNER
    FAMILY.id -> FAMILY
    FRIEND.id -> FRIEND
    CARE_TAKER.id -> CARE_TAKER
    else -> VISITOR
}

fun Int.toNotificationType() = when (this) {
    INVITE.id -> INVITE
    PET_FOUND.id -> PET_FOUND
    else -> UNKNOWN
}

fun String.toUri() = Uri.parse(this)

@ColorRes
@AttrRes
fun Int.toColorId(context: Context): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(this, typedValue, false)
    return typedValue.data
}

fun Float.toPx(res: Resources) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    res.displayMetrics
)

fun Float.toDp(res: Resources) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_PX,
    this,
    res.displayMetrics
)