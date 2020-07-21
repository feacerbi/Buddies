package com.buddies.server.api

import android.net.Uri
import com.buddies.common.model.*
import com.buddies.common.model.OwnershipCategory.VISITOR
import com.buddies.common.util.generateNewId
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.toOwner
import com.buddies.server.util.toOwnerships
import com.buddies.server.util.toPet

class PetApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val ownershipsRepository: OwnershipsRepository
): BaseApi() {

    suspend fun getPetsFromCurrentUser() = getPetsFromUser(usersRepository.getCurrentUserId())

    suspend fun getPetsFromUser(
        userId: String
    ) = runWithResult {

        val ownerships = ownershipsRepository.getUserOwnerships(userId)
            .handleTaskResult()
            .toOwnerships()

        ownerships.map { ownership ->
            petsRepository.getPet(ownership.info.petId)
                .handleTaskResult()
                .toPet()
        }
    }

    suspend fun getPet(
        petId: String
    ) = runWithResult {
        petsRepository.getPet(petId)
            .handleTaskResult()
            .toPet()
    }

    suspend fun updateName(
        petId: String,
        name: String
    ) = runTransactionsWithResult(
        petsRepository.updateName(petId, name)
    )

    suspend fun updateTag(
        petId: String,
        tag: String
    ) = runTransactionsWithResult(
        petsRepository.updateTag(petId, tag)
    )

    suspend fun updateAnimal(
        petId: String,
        animal: String,
        breed: String
    ) = runTransactionsWithResult(
        petsRepository.updateAnimal(petId, animal),
        petsRepository.updateBreed(petId, breed)
    )

    suspend fun updatePhoto(
        petId: String,
        photo: Uri
    ) = runTransactionsWithResult(
        petsRepository.updatePhoto(petId, photo.toString())
    )

    suspend fun addNewPet(
        petInfo: PetInfo,
        category: OwnershipCategory
    ) = addNewPet(petInfo, usersRepository.getCurrentUserId(), category)

    private suspend fun addNewPet(
        petInfo: PetInfo,
        userId: String,
        category: OwnershipCategory
    ) = runWithResult {
        val newPetId = generateNewId()

        runTransactionsWithResult(
            petsRepository.addPet(newPetId, petInfo),
            ownershipsRepository.addOwnership(
                OwnershipInfo(
                    newPetId,
                    userId,
                    category.name
                )
            )
        )
    }

    suspend fun updatePetOwnership(
        petId: String,
        userId: String,
        category: String
    ) = runWithResult {
        val ownership = ownershipsRepository.getOwnership(petId, userId)
            .handleTaskResult()
            .toOwnerships()
            .first()

        runTransactionsWithResult(
            when (category) {
                VISITOR.name -> ownershipsRepository.removeOwnership(ownership.id)
                else -> ownershipsRepository.updateOwnership(ownership.id, category)
            }
        )
    }

    suspend fun addNewPetOwnership(
        pet: Pet,
        user: User,
        category: OwnershipCategory
    ) = runTransactionsWithResult(
        ownershipsRepository.addOwnership(
            OwnershipInfo(
                pet.id,
                user.id,
                category.name
            )
        )
    )

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
            OwnershipInfo(petId, userId, VISITOR.name)
        }
    }

    suspend fun deletePet(
        pet: Pet
    ) = runWithResult {
        val ownerships = ownershipsRepository.getPetOwnerships(pet.id)
            .handleTaskResult()
            .toOwnerships()

        val deleteOwnerships = ownerships.map { ownership ->
            ownershipsRepository.removeOwnership(ownership.id)
        }.toTypedArray()

        runTransactionsWithResult(
            petsRepository.deletePet(pet.id),
            *deleteOwnerships
        )
    }
}