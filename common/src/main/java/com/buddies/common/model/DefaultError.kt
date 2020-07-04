package com.buddies.common.model

data class DefaultError(
    val code: ErrorCode,
    val details: List<DefaultError>? = null,
    val innerError: Map<String, Any>? = null
)