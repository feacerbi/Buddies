package com.buddies.common.model

data class UserInfo(
    var name: String? = null,
    var email: String? = null,
    var photo: String? = null
) {
    fun toUser(id: String) = User(id, this)
}