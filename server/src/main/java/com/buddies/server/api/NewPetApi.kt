package com.buddies.server.api

import com.buddies.common.model.NewPet
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.model.PetInfo
import com.buddies.common.util.generateNewId
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.TagsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.getDownloadUrl
import com.buddies.server.util.toTag

class NewPetApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val tagsRepository: TagsRepository,
    private val ownershipsRepository: OwnershipsRepository
) : BaseApi() {

    suspend fun getTagByValue(
        tagValue: String
    ) = runWithResult {
        tagsRepository.getTagByValue(tagValue)
            .handleTaskResult()
            .toTag()
    }

    suspend fun addNewPet(
        newPet: NewPet,
        categoryId: Int
    ) = addNewPet(newPet, usersRepository.getCurrentUserId(), categoryId)

    private suspend fun addNewPet(
        newPet: NewPet,
        userId: String,
        categoryId: Int
    ) = runWithResult {
        val newPetId = generateNewId()

        val uploadResult = petsRepository.uploadProfileImage(newPetId, newPet.photo)
            .handleTaskResult()

        val downloadUri = uploadResult.getDownloadUrl()
            .handleTaskResult()

        val petInfo = PetInfo(
            newPet.tag,
            newPet.name,
            downloadUri.toString(),
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
}