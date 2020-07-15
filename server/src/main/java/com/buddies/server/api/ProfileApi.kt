package com.buddies.server.api

import android.net.Uri
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.getDownloadUrl
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
        photoUri: Uri
    ) = runWithResult {

        val uploadResult = usersRepository.uploadImage(photoUri)
            .handleTaskResult()

        val downloadUri = uploadResult.getDownloadUrl()
            .handleTaskResult()

        runTransactionsWithResult(
            usersRepository.updatePhoto(downloadUri.toString())
        )
    }

    fun logout() = usersRepository.logout()
}