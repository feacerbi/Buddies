package com.buddies.profile.usecase

import android.net.Uri
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.ProfileApi

class ProfileUseCases(
    private val profileApi: ProfileApi
) : BaseUseCases() {

    suspend fun updateName(name: String) = request {
        profileApi.updateName(name)
    }

    suspend fun updatePhoto(photo: Uri) = request {
        profileApi.updatePhoto(photo)
    }

    suspend fun getCurrentUser() = request {
        profileApi.getCurrentUser()
    }

    fun logout() = profileApi.logout()
}