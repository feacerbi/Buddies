package com.buddies.server.api

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.buddies.common.model.*
import com.buddies.common.model.OwnershipCategory.VISITOR
import com.buddies.common.util.generateNewId
import com.buddies.server.repository.*
import com.buddies.server.util.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

class PetApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val animalsRepository: AnimalsRepository,
    private val breedsRepository: BreedsRepository,
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
        animalId: String,
        breedId: String
    ) = runTransactionsWithResult(
        petsRepository.updateAnimal(petId, animalId),
        petsRepository.updateBreed(petId, breedId)
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
        usersRepository.getAllUsers(pageSize, query, start)
            .handleTaskResult()
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
    }
}