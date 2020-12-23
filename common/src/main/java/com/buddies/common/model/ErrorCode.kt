package com.buddies.common.model

import androidx.annotation.StringRes
import com.buddies.common.R

enum class ErrorCode(
    @StringRes val message: Int
) {
    UNKNOWN(R.string.unknown_error_message),
    TASK_FAIL(R.string.fail_task),
    TASK_NULL(R.string.null_task),
    RESULT_NULL(R.string.null_result),
    ACCESS_DENIED(R.string.access_denied_error_code),
    INVALID_TAG(R.string.invalid_tag_error),
    UNKNOWN_NOTIFICATION_TYPE(R.string.unknown_notification_error)
}