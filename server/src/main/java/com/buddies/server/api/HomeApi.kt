package com.buddies.server.api

import com.buddies.common.model.InfoType
import com.buddies.common.model.NotificationType
import com.buddies.server.model.NotificationInfo
import com.buddies.server.repository.NotificationsRepository
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.keysToString
import com.buddies.server.util.toOwnerships
import com.buddies.server.util.toPet
import com.buddies.server.util.toUser

class HomeApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val ownershipsRepository: OwnershipsRepository,
    private val notificationsRepository: NotificationsRepository
) : BaseApi() {

    suspend fun getCurrentUserInfo() = runWithResult {
        usersRepository.getCurrentUser()
            .handleTaskResult()
            .toUser()
            .info
    }

    suspend fun getPet(
        tagId: String
    ) = runWithResult {
        val queryResult = petsRepository.getPetByTag(tagId)
            .handleTaskResult()
            .documents

        if (queryResult.size > 0) {
            queryResult[0].toPet()
        } else {
            null
        }
    }

    suspend fun notifyPetFound(
        petId: String,
        shareInfoList: Map<InfoType, String>
    ) = runWithResult {
        val ownerships = ownershipsRepository.getPetOwnerships(petId)
            .handleTaskResult()
            .toOwnerships()

        val addNotifications = ownerships.map { ownership ->
            notificationsRepository.addNotification(
                NotificationInfo(
                    type = NotificationType.PET_FOUND.id,
                    targetUserId = ownership.info.userId,
                    sourceUserId = usersRepository.getCurrentUserId(),
                    petId = petId,
                    extra = shareInfoList.keysToString()
                )
            )
        }.toTypedArray()

        runTransactions(
            *addNotifications
        )
    }
}