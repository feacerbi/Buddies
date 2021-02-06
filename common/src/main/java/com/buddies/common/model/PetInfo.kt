package com.buddies.common.model

data class PetInfo(
    val tag: String = "",
    val name: String = "",
    val photo: String = "",
    val animal: String = "",
    val breed: String = "",
    val lost: Boolean = false
) {
    override fun equals(other: Any?): Boolean =
        if (other is PetInfo) {
            tag == other.tag &&
                name == other.name &&
                photo == other.photo &&
                animal == other.animal &&
                breed == other.breed &&
                lost == other.lost
        } else false

    override fun hashCode(): Int =
        tag.hashCode() + name.hashCode() + photo.hashCode() + animal.hashCode() +
            breed.hashCode() + lost.hashCode()
}