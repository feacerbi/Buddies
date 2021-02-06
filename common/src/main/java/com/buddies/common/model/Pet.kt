package com.buddies.common.model

data class Pet(
    val id: String,
    val info: PetInfo
) {
    override fun equals(other: Any?): Boolean =
        if (other is Pet) {
            id == other.id &&
                info == other.info
        } else false

    override fun hashCode(): Int =
        id.hashCode() + info.hashCode()
}