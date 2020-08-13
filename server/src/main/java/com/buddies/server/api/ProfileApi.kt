package com.buddies.server.api

import android.net.Uri
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.model.UserNotification
import com.buddies.common.util.toNotificationType
import com.buddies.common.util.toOwnershipCategory
import com.buddies.server.repository.NotificationsRepository
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.*

class ProfileApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val ownershipsRepository: OwnershipsRepository,
    private val notificationsRepository: NotificationsRepository
) : BaseApi() {

    suspend fun getCurrentUser(
    ) = runWithResult {
        usersRepository.getCurrentUser()
            .handleTaskResult()
            .toUser()
    }

    suspend fun getUser(
        userId: String
    ) = runWithResult {
        usersRepository.getUser(userId)
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

    suspend fun getCurrentUserNotifications(
    ) = runWithResult {
        val notifications = notificationsRepository.getCurrentUserNotifications()
            .handleTaskResult()
            .toNotifications()

        notifications.map {
            UserNotification(
                it.id,
                usersRepository.getUser(it.info.inviterId).handleTaskResult().toUser().info.name,
                petsRepository.getPet(it.info.petId).handleTaskResult().toPet(),
                it.info.category.toOwnershipCategory(),
                it.info.type.toNotificationType(),
                it.info.unread,
                it.info.timestamp.toDate()
            )
        }
    }

    suspend fun removeNotification(
        notificationId: String
    ) = runTransactionsWithResult(
        notificationsRepository.removeNotification(notificationId)
    )

    suspend fun acceptInvitation(
        notificationId: String
    ) = runWithResult {
        val notification = notificationsRepository.getNotification(notificationId)
            .handleTaskResult()
            .toNotification()

        runTransactionsWithResult(
            ownershipsRepository.addOwnership(
                OwnershipInfo(
                    notification.info.petId,
                    usersRepository.getCurrentUserId(),
                    notification.info.category)),
            notificationsRepository.removeNotification(notification.id)
        )
    }

    fun logout() = usersRepository.logout()
}