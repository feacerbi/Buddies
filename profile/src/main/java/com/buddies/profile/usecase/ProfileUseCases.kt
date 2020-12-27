package com.buddies.profile.usecase

import android.net.Uri
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.NotificationsApi
import com.buddies.server.api.ProfileApi

class ProfileUseCases(
    private val profileApi: ProfileApi,
    private val notificationsApi: NotificationsApi
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

    suspend fun getNotifications() = request {
        notificationsApi.getCurrentUserNotifications()
    }

    suspend fun ignoreInvitation(notificationId: String) = request {
        notificationsApi.removeNotification(notificationId)
    }

    suspend fun acceptInvitation(notificationId: String) = request {
        notificationsApi.acceptInvitation(notificationId)
    }

    fun logout() = profileApi.logout()
}