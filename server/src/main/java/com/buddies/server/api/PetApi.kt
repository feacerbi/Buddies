package com.buddies.server.api

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.buddies.common.model.Buddy
import com.buddies.common.model.NotificationType.INVITE
import com.buddies.common.model.Owner
import com.buddies.common.model.OwnershipAccess.EDIT_ALL
import com.buddies.common.model.OwnershipCategory.VISITOR
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.util.handleAccessResult
import com.buddies.common.util.toOwnershipCategory
import com.buddies.server.model.NotificationInfo
import com.buddies.server.repository.AnimalsRepository
import com.buddies.server.repository.BreedsRepository
import com.buddies.server.repository.GalleryRepository
import com.buddies.server.repository.NotificationsRepository
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.TagsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.BaseDataSource.Companion.DEFAULT_PAGE_SIZE
import com.buddies.server.util.OwnersDataSource
import com.buddies.server.util.getDownloadUrl
import com.buddies.server.util.toAnimal
import com.buddies.server.util.toBreed
import com.buddies.server.util.toOwner
import com.buddies.server.util.toOwnerships
import com.buddies.server.util.toPet
import com.buddies.server.util.toTag
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

class PetApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val animalsRepository: AnimalsRepository,
    private val breedsRepository: BreedsRepository,
    private val ownershipsRepository: OwnershipsRepository,
    private val tagsRepository: TagsRepository,
    private val galleryRepository: GalleryRepository,
    private val notificationsRepository: NotificationsRepository
): BaseApi() {

    suspend fun getBuddiesFromCurrentUser() = getBuddiesFromUser(usersRepository.getCurrentUserId())

    suspend fun getBuddiesFromUser(
        userId: String
    ) = runWithResult {
        val ownerships = ownershipsRepository.getUserOwnerships(userId)
            .handleTaskResult()
            .toOwnerships()

        ownerships.map { ownership ->
            Buddy(
                petsRepository.getPet(ownership.info.petId)
                .handleTaskResult()
                .toPet(),
                ownership.info.category.toOwnershipCategory()
            )
        }
    }

    suspend fun getPet(
        petId: String
    ) = runWithResult {
        petsRepository.getPet(petId)
            .handleTaskResult()
            .toPet()
    }

    suspend fun getAnimalAndBreed(
        animalId: String,
        breedId: String
    ) = runWithResult {
        Pair(
            animalsRepository.getAnimal(animalId)
                .handleTaskResult()
                .toAnimal(),
            breedsRepository.getBreed(breedId)
                .handleTaskResult()
                .toBreed()
        )
    }

    suspend fun updateName(
        petId: String,
        name: String
    ) = runWithResult {
        checkAccess(petId)

        runTransactions(
            petsRepository.updateName(petId, name)
        )
    }

    suspend fun updateTag(
        petId: String,
        tag: String
    ) = runWithResult {
        checkAccess(petId)

        val pet = petsRepository.getPet(petId)
            .handleTaskResult()
            .toPet()

        runTransactions(
            tagsRepository.markTagAvailable(pet.info.tag),
            tagsRepository.markTagUnavailable(tag),
            petsRepository.updateTag(petId, tag)
        )
    }

    suspend fun updateAnimal(
        petId: String,
        animalId: String,
        breedId: String
    ) = runWithResult {
        checkAccess(petId)

        runTransactions(
            petsRepository.updateAnimal(petId, animalId),
            petsRepository.updateBreed(petId, breedId)
        )
    }

    suspend fun updatePhoto(
        petId: String,
        photo: Uri
    ) = runWithResult {
        checkAccess(petId)

        val uploadResult = petsRepository.uploadProfileImage(petId, photo)
            .handleTaskResult()

        val downloadUri = uploadResult.getDownloadUrl()
            .handleTaskResult()

        runTransactions(
            petsRepository.updatePhoto(petId, downloadUri.toString())
        )
    }

    suspend fun updatePetOwnership(
        petId: String,
        userId: String,
        category: Int
    ) = runWithResult {
        checkAccess(petId)

        val ownership = ownershipsRepository.getOwnership(petId, userId)
            .handleTaskResult()
            .toOwnerships()
            .first()

        runTransactions(
            when (category) {
                VISITOR.id -> ownershipsRepository.removeOwnership(ownership.id)
                else -> ownershipsRepository.updateOwnership(ownership.id, category)
            }
        )
    }

    suspend fun updatePetLostStatus(
        petId: String,
        lost: Boolean
    ) = runWithResult {
        checkAccess(petId)

        runTransactions(
            petsRepository.updateLost(petId, lost)
        )
    }

    suspend fun getPetTag(
        tagId: String
    ) = runWithResult {
        tagsRepository.getTagById(tagId)
            .handleTaskResult()
            .toTag()
    }

    suspend fun getPetOwners(
        petId: String
    ) = runWithResult {
        val ownerships = ownershipsRepository.getPetOwnerships(petId)
            .handleTaskResult()
            .toOwnerships()

        ownerships.map { ownership ->
            usersRepository.getUser(ownership.info.userId)
                .handleTaskResult()
                .toOwner(ownership)
        }
    }

    suspend fun getCurrentUserPetOwnership(
        petId: String
    ) = getUserPetOwnership(usersRepository.getCurrentUserId(), petId)

    suspend fun getUserPetOwnership(
        userId: String,
        petId: String
    ) = runWithResult {
        val ownerships = ownershipsRepository.getPetOwnerships(petId)
            .handleTaskResult()
            .toOwnerships()

        val ownership = ownerships.filter { it.info.userId == userId }

        if (ownership.isNotEmpty()) {
            ownership[0].info
        } else {
            OwnershipInfo(petId, userId, VISITOR.id)
        }
    }

    // TODO Add delete pet feature
    suspend fun deletePet(
        petId: String
    ) = runWithResult {
        checkAccess(petId)

        val ownerships = ownershipsRepository.getPetOwnerships(petId)
            .handleTaskResult()
            .toOwnerships()

        val deleteOwnerships = ownerships.map { ownership ->
            ownershipsRepository.removeOwnership(ownership.id)
        }.toTypedArray()

        galleryRepository.deleteGallery(petId)

        runTransactions(
            petsRepository.deletePet(petId),
            *deleteOwnerships
        )
    }

    suspend fun inviteOwner(
        userId: String,
        petId: String,
        category: Int
    ) = runWithResult {
        checkAccess(petId)

        runTransactions(
            notificationsRepository.addNotification(
                NotificationInfo(
                    type = INVITE.id,
                    targetUserId = userId,
                    sourceUserId = usersRepository.getCurrentUserId(),
                    petId = petId,
                    ownershipCategory = category,
                    unread = true)
            )
        )
    }

    suspend fun getOwnersFlowWithPaging(
        petId: String,
        query: String,
        pageSize: Int = -1
    ): Flow<PagingData<Owner>> {

        val ownerships = ownershipsRepository.getPetOwnerships(petId)
            .handleTaskResult()
            .toOwnerships()

        val ownersDataSource = OwnersDataSource(ownerships, query, ::getAllUsersWithPaging)

        return Pager(PagingConfig(if (pageSize != -1) pageSize else DEFAULT_PAGE_SIZE)) {
            ownersDataSource
        }.flow
    }

    private suspend fun getAllUsersWithPaging(
        pageSize: Int,
        query: String,
        start: DocumentSnapshot? = null
    ) = runWithResult {
        usersRepository.getUsers(pageSize.toLong(), query, start)
            .handleTaskResult()
    }

    private suspend fun checkAccess(
        petId: String
    ) = runWithResult {
        val ownership =
            ownershipsRepository.getOwnership(petId, usersRepository.getCurrentUserId())
                .handleTaskResult()
                .toOwnerships()
                .firstOrNull()

        ownership?.info?.category?.toOwnershipCategory()?.access == EDIT_ALL
    }.handleAccessResult()
}