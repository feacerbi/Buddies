package com.buddies.server.api

import com.buddies.common.model.*
import com.buddies.common.util.generateNewId
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.toOwnerships
import com.buddies.server.util.toPet
import com.buddies.server.util.toUser

class MyPetsApi(
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

    suspend fun removePetOwnership(
        ownership: Ownership
    ) = runTransactionsWithResult(
        ownershipsRepository.removeOwnership(ownership.id)
    )

    suspend fun getPetOwners(
        pet: Pet
    ) = runWithResult {
        val ownerships = ownershipsRepository.getPetOwnerships(pet.id)
            .handleTaskResult()
            .toOwnerships()

        ownerships.map { ownership ->
            usersRepository.getUser(ownership.info.userId)
                .handleTaskResult()
                .toUser()
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