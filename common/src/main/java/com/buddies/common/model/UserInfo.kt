package com.buddies.common.model

data class UserInfo(
    var name: String = "",
    var email: String = "",
    var photo: String = ""
) {
    fun toUser(id: String) = User(id, this)
}