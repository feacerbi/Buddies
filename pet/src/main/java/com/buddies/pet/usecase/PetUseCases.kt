package com.buddies.pet.usecase

import android.net.Uri
import com.buddies.common.model.OwnershipCategory
import com.buddies.common.model.User
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.AnimalApi
import com.buddies.server.api.PetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn

class PetUseCases(
    private val petApi: PetApi,
    private val animalApi: AnimalApi
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
        petApi.updatePetOwnership(petId, userId, ownershipCategory.id)
    }

    suspend fun getAllAnimals() = request {
        animalApi.getAllAnimals()
    }

    suspend fun getBreedsFromAnimal(
        animalId: String
    ) = request {
        animalApi.getAllAnimalBreeds(animalId)
    }

    suspend fun getPetTag(
        tagId: String
    ) = request {
        petApi.getPetTag(tagId)
    }

    @ExperimentalCoroutinesApi
    suspend fun getOwnersToInvite(
        petId: String,
        query: String
    ) = petApi.getOwnersFlowWithPaging(petId, query)
        .flowOn(Dispatchers.IO)

    suspend fun inviteOwner(
        userId: String,
        petId: String,
        category: OwnershipCategory
    ) = request {
        petApi.inviteOwner(userId, petId, category.id)
    }
}