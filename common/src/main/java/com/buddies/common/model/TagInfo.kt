package com.buddies.common.model

data class TagInfo(
    val value: String = "",
    val encrypted: String = "",
    val available: Boolean = false
) {
    override fun equals(other: Any?): Boolean =
        if (other is TagInfo) {
            value == other.value &&
                encrypted == other.encrypted &&
                available == other.available
        } else false

    override fun hashCode(): Int =
        value.hashCode() + encrypted.hashCode() + available.hashCode()
}