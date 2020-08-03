package com.buddies.mypets.usecase

import android.net.Uri
import com.buddies.common.model.OwnershipCategory
import com.buddies.common.model.PetInfo
import com.buddies.common.model.User
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.PetApi

class PetUseCases(
    private val petApi: PetApi
) : BaseUseCases() {

    suspend fun getPetsFromCurrentUser() = request {
        petApi.getPetsFromCurrentUser()
    }

    suspend fun getPetsFromUser(user: User) = request {
        petApi.getPetsFromUser(user.id)
    }

    suspend fun getOwnersFromPet(petId: String) = request {
        petApi.getPetOwners(petId)
    }

    suspend fun getCurrentUserPetOwnership(petId: String) = request {
        petApi.getCurrentUserPetOwnership(petId)
    }

    suspend fun addNewPet(
        petInfo: PetInfo,
        category: OwnershipCategory
    ) = request {
        petApi.addNewPet(petInfo, category)
    }

    suspend fun getPet(
        petId: String
    ) = request {
        petApi.getPet(petId)
    }

    suspend fun getAnimalAndBreed(
        animalId: String,
        breedId: String
    ) = request {
        petApi.getAnimalAndBreed(animalId, breedId)
    }

    suspend fun updatePetName(
        petId: String,
        name: String
    ) = request {
        petApi.updateName(petId, name)
    }

    suspend fun updatePetTag(
        petId: String,
        tag: String
    ) = request {
        petApi.updateTag(petId, tag)
    }

    suspend fun updatePetAnimal(
        petId: String,
        animalId: String,
        breedId: String
    ) = request {
        petApi.updateAnimal(petId, animalId, breedId)
    }

    suspend fun updatePetPhoto(
        petId: String,
        photo: Uri
    ) = request {
        petApi.updatePhoto(petId, photo)
    }

    suspend fun updateOwnership(
        petId: String,
        userId: String,
        ownershipCategory: OwnershipCategory
    ) = request {
        petApi.updatePetOwnership(petId, userId, ownershipCategory.name)
    }
}