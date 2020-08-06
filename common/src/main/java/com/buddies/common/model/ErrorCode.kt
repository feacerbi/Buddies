package com.buddies.common.model

import com.buddies.common.R

enum class ErrorCode(
    val message: Int
) {
    UNKNOWN(R.string.unknown_error_message),
    TASK_FAIL(R.string.fail_task),
    TASK_NULL(R.string.null_task),
    RESULT_NULL(R.string.null_result),
    ACCESS_DENIED(R.string.access_denied_error_code)
}