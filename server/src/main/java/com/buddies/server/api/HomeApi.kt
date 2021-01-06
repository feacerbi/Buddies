package com.buddies.server.api

import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode.INVALID_TAG
import com.buddies.common.model.NotificationType
import com.buddies.server.model.NotificationInfo
import com.buddies.server.repository.NotificationsRepository
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.TagsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.toOwnerships
import com.buddies.server.util.toPet
import com.buddies.server.util.toUser

class HomeApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val tagsRepository: TagsRepository,
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
            throw DefaultErrorException(DefaultError(INVALID_TAG))
        }
    }

    suspend fun notifyPetFound(
        petId: String,
        shareInfoList: Map<String, String>
    ) = runWithResult {
        val ownerships = ownershipsRepository.getPetOwnerships(petId)
            .handleTaskResult()
            .toOwnerships()

        val addNotifications = ownerships.map {
            notificationsRepository.addNotification(
                NotificationInfo(
                    type = NotificationType.PET_FOUND.id,
                    targetUserId = it.info.userId,
                    sourceUserId = usersRepository.getCurrentUserId(),
                    petId = petId,
                    extra = shareInfoList
                )
            )
        }.toTypedArray()

        runTransactions(
            *addNotifications
        )
    }
}