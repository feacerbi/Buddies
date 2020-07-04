package com.buddies.server.api

import android.net.Uri
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.toUser

class ProfileApi(
    private val usersRepository: UsersRepository
) : BaseApi() {

    suspend fun getCurrentUser(
    ) = runWithResult {
        usersRepository.getCurrentUser()
            .handleTaskResult()
            .toUser()
    }

    suspend fun updateName(
        name: String
    ) = runTransactionsWithResult(
        usersRepository.updateName(name)
    )

    suspend fun updatePhoto(
        photo: Uri
    ) = runTransactionsWithResult(
        usersRepository.updatePhoto(photo)
    )

    fun logout() = usersRepository.logout()
}