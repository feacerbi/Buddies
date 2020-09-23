package com.buddies.server.api

import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode.INVALID_TAG
import com.buddies.common.model.NotificationType
import com.buddies.server.model.NotificationInfo
import com.buddies.server.repository.*
import com.buddies.server.util.toOwnerships
import com.buddies.server.util.toPet
import com.buddies.server.util.toTag

class HomeApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val tagsRepository: TagsRepository,
    private val ownershipsRepository: OwnershipsRepository,
    private val notificationsRepository: NotificationsRepository
) : BaseApi() {

    suspend fun getPetByTag(
        tagValue: String
    ) = runWithResult {
        val tag = tagsRepository.getTagByValue(tagValue)
            .handleTaskResult()
            .toTag()

        val queryResult = petsRepository.getPetByTag(tag.id)
            .handleTaskResult()
            .documents

        if (queryResult.size > 0) {
            queryResult[0].toPet()
        } else {
            throw DefaultErrorException(DefaultError(INVALID_TAG))
        }
    }

    suspend fun notifyPetFound(
        petId: String
    ) = runWithResult {
        val ownerships = ownershipsRepository.getPetOwnerships(petId)
            .handleTaskResult()
            .toOwnerships()

        val addNotifications = ownerships.map {
            notificationsRepository.addNotification(
                NotificationInfo(
                    NotificationType.PET_FOUND.id,
                    it.info.userId,
                    usersRepository.getCurrentUserId(),
                    petId
                )
            )
        }.toTypedArray()

        runTransactionsWithResult(
            *addNotifications
        )
    }
}