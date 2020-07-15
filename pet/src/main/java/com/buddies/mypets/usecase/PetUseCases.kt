package com.buddies.mypets.usecase

import android.net.Uri
import com.buddies.common.model.*
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
        animal: Animal,
        breed: Breed
    ) = request {
        petApi.updateAnimal(petId, animal.name, breed.name)
    }

    suspend fun updatePetPhoto(
        petId: String,
        photo: Uri
    ) = request {
        petApi.updatePhoto(petId, photo)
    }
}