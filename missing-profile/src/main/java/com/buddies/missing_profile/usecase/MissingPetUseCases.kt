package com.buddies.missing_profile.usecase

import android.net.Uri
import com.buddies.common.usecase.BaseUseCases
import com.buddies.contact.model.ShareInfo
import com.buddies.server.api.AnimalApi
import com.buddies.server.api.MissingPetApi

class MissingPetUseCases(
    private val missingPetApi: MissingPetApi,
    private val animalApi: AnimalApi
) : BaseUseCases() {

    suspend fun getCurrentUser() = request {
        missingPetApi.getCurrentUser()
    }

    suspend fun getUser(
        userId: String
    ) = request {
        missingPetApi.getUser(userId)
    }

    suspend fun getPet(
        petId: String
    ) = request {
        missingPetApi.getPet(petId)
    }

    suspend fun getAnimalAndBreed(
        animalId: String,
        breedId: String
    ) = request {
        missingPetApi.getAnimalAndBreed(animalId, breedId)
    }

    suspend fun updatePetName(
        petId: String,
        name: String
    ) = request {
        missingPetApi.updateName(petId, name)
    }

    suspend fun updatePetAnimal(
        petId: String,
        animalId: String,
        breedId: String
    ) = request {
        missingPetApi.updateAnimal(petId, animalId, breedId)
    }

    suspend fun updatePetPhoto(
        petId: String,
        photo: Uri
    ) = request {
        missingPetApi.updatePhoto(petId, photo)
    }

    suspend fun updatePetContactInfo(
        petId: String,
        shareInfo: List<ShareInfo>
    ) = request {
        missingPetApi.updateContactInfo(petId, shareInfo.map { it.toPair() }.toMap())
    }

    suspend fun updatePetLocation(
        petId: String,
        latitude: Double,
        longitude: Double
    ) = request {
        missingPetApi.updateLocation(petId, latitude, longitude)
    }

    suspend fun removePet(
        petId: String
    ) = request {
        missingPetApi.removeMissingPet(petId)
    }

    suspend fun markPetAsReturned(
        petId: String
    ) = request {
        missingPetApi.markAsReturned(petId)
    }

    suspend fun getAllAnimals() = request {
        animalApi.getAllAnimals()
    }

    suspend fun getBreedsFromAnimal(
        animalId: String
    ) = request {
        animalApi.getAllAnimalBreeds(animalId)
    }
}