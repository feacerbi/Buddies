package com.buddies.server.api

import android.net.Uri
import com.buddies.common.model.MissingPetInfo
import com.buddies.common.model.NewPet
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.model.PetInfo
import com.buddies.common.util.generateNewId
import com.buddies.server.repository.MissingPetsRepository
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.TagsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.getDownloadUrl

class NewPetApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val missingPetsRepository: MissingPetsRepository,
    private val tagsRepository: TagsRepository,
    private val ownershipsRepository: OwnershipsRepository
) : BaseApi() {

    suspend fun addNewPet(
        newPet: NewPet,
        categoryId: Int
    ) = addNewPet(newPet, usersRepository.getCurrentUserId(), categoryId)

    suspend fun addMissingPet(
        newPet: NewPet
    ) = addMissingPet(newPet, usersRepository.getCurrentUserId())

    private suspend fun addNewPet(
        newPet: NewPet,
        userId: String,
        categoryId: Int
    ) = runWithResult {
        val newPetId = generateNewId()

        var downloadUri = ""

        if (newPet.photo != Uri.EMPTY) {
            val uploadResult = petsRepository.uploadProfileImage(newPetId, newPet.photo)
                .handleTaskResult()

            downloadUri = uploadResult.getDownloadUrl()
                .handleTaskResult()
                .toString()
        }

        val petInfo = PetInfo(
            newPet.tag,
            newPet.name,
            downloadUri,
            newPet.animal?.id ?: "",
            newPet.breed?.id ?: ""
        )

        runTransactions(
            petsRepository.addPet(newPetId, petInfo),
            tagsRepository.markTagUnavailable(petInfo.tag),
            ownershipsRepository.addOwnership(
                OwnershipInfo(
                    newPetId,
                    userId,
                    categoryId
                )
            )
        )
    }

    private suspend fun addMissingPet(
        newPet: NewPet,
        userId: String
    ) = runWithResult {
        val newPetId = generateNewId()

        var downloadUri = ""

        if (newPet.photo != Uri.EMPTY) {
            val uploadResult = missingPetsRepository.uploadProfileImage(newPetId, newPet.photo)
                .handleTaskResult()

            downloadUri = uploadResult.getDownloadUrl()
                .handleTaskResult()
                .toString()
        }

        val petInfo = MissingPetInfo(
            newPet.name,
            downloadUri,
            newPet.animal?.id ?: "",
            newPet.breed?.id ?: "",
            userId
        )

        runTransactions(
            missingPetsRepository.addPet(newPetId, petInfo),
        )
    }
}