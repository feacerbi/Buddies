package com.buddies.common.model

data class Tag(
    val id: String,
    val info: TagInfo
) {
    override fun equals(other: Any?): Boolean =
        if (other is Tag) {
            id == other.id &&
                info == other.info
        } else false

    override fun hashCode(): Int =
        id.hashCode() + info.hashCode()
}