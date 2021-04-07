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
import com.buddies.common.model.InfoType
import com.buddies.common.model.InfoType.EMAIL
import com.buddies.common.model.InfoType.LOCATION
import com.buddies.common.model.InfoType.NAME
import com.buddies.common.model.InfoType.PHONE
import com.buddies.common.model.NotificationType
import com.buddies.common.model.NotificationType.INVITE
import com.buddies.common.model.NotificationType.PET_FOUND
import com.buddies.common.model.OwnershipCategory.CARE_TAKER
import com.buddies.common.model.OwnershipCategory.FAMILY
import com.buddies.common.model.OwnershipCategory.FRIEND
import com.buddies.common.model.OwnershipCategory.OWNER
import com.buddies.common.model.OwnershipCategory.VISITOR
import java.util.*

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
    else -> NotificationType.UNKNOWN
}

fun String.toInfoType() = when (this) {
    NAME.name.toLowerCase(Locale.getDefault()) -> NAME
    EMAIL.name.toLowerCase(Locale.getDefault()) -> EMAIL
    PHONE.name.toLowerCase(Locale.getDefault()) -> PHONE
    LOCATION.name.toLowerCase(Locale.getDefault()) -> LOCATION
    else -> InfoType.UNKNOWN
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