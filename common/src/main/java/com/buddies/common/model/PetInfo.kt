package com.buddies.common.model

data class PetInfo(
    val tag: String = "",
    val name: String = "",
    val photo: String = "",
    val type: String = "",
    val breed: String = ""
) {
    fun toPet(id: String) = Pet(id, this)
}