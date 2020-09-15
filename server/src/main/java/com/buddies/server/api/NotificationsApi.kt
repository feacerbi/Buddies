package com.buddies.server.api

import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode.UNKNOWN_NOTIFICATION_TYPE
import com.buddies.common.model.NotificationType.INVITE
import com.buddies.common.model.NotificationType.PET_FOUND
import com.buddies.common.model.Result
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import com.buddies.common.model.UserNotification
import com.buddies.common.util.mapResult
import com.buddies.common.util.toDefaultError
import com.buddies.common.util.toNotificationType
import com.buddies.server.model.Notification
import com.buddies.server.repository.NotificationsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class NotificationsApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val notificationsRepository: NotificationsRepository
) : BaseApi() {

    @ExperimentalCoroutinesApi
    suspend fun listenToCurrentUserNotifications(
    ): Flow<Result<List<UserNotification>?>> = callbackFlow<Result<List<Notification>>> {
        notificationsRepository.queryCurrentUserNotifications().addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                sendBlocking(Fail(exception.toDefaultError()))
            }

            try {
                sendBlocking(Success(snapshot?.toNotifications()))
            } catch (e: Exception) {
                sendBlocking(Fail(e.toDefaultError()))
            }
        }
        awaitClose()
    }.map { result ->
        result.mapResult { list ->
            list?.map {
                val user = usersRepository.getUser(it.info.sourceUserId)
                    .handleTaskResult()
                    .toUser()
                val pet = petsRepository.getPet(it.info.petId)
                    .handleTaskResult()
                    .toPet()

                when (it.info.type.toNotificationType()) {
                    INVITE -> it.toInviteNotification(user, pet)
                    PET_FOUND -> it.toPetFoundNotification(user, pet)
                    else -> throw DefaultErrorException(DefaultError(UNKNOWN_NOTIFICATION_TYPE))
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun markNotificationAsRead(
        notificationId: String
    ) = runTransactionsWithResult(
        notificationsRepository.markAsRead(notificationId)
    )
}