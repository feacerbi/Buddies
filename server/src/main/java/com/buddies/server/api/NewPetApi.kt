package com.buddies.server.api

import android.net.Uri
import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode.RESULT_NULL
import com.buddies.common.model.NewPet
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.model.PetInfo
import com.buddies.common.util.generateNewId
import com.buddies.server.repository.OwnershipsRepository
import com.buddies.server.repository.PetsRepository
import com.buddies.server.repository.TagsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.getDownloadUrl

class NewPetApi(
    private val usersRepository: UsersRepository,
    private val petsRepository: PetsRepository,
    private val tagsRepository: TagsRepository,
    private val ownershipsRepository: OwnershipsRepository
) : BaseApi() {

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

        val photo = newPet.photo
        val tag = newPet.tag
        val name = newPet.name
        val animal = newPet.animal
        val breed = newPet.breed

        if (tag == null || name == null || animal == null || breed == null) {
                throw DefaultErrorException(DefaultError(RESULT_NULL))
        }

        var downloadUri = ""

        if (photo != null && photo != Uri.EMPTY) {
            val uploadResult = petsRepository.uploadProfileImage(newPetId, photo)
                .handleTaskResult()

            downloadUri = uploadResult.getDownloadUrl()
                .handleTaskResult()
                .toString()
        }

        val petInfo = PetInfo(tag, name, downloadUri, animal.id, breed.id)

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