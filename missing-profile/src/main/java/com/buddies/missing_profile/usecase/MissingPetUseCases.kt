package com.buddies.missing_profile.usecase

import android.net.Uri
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.AnimalApi
import com.buddies.server.api.MissingPetApi

class MissingPetUseCases(
    private val missingPetApi: MissingPetApi,
    private val animalApi: AnimalApi
) : BaseUseCases() {

    suspend fun getCurrentUser() = request {
        missingPetApi.getCurrentUser()
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

    suspend fun getAllAnimals() = request {
        animalApi.getAllAnimals()
    }

    suspend fun getBreedsFromAnimal(
        animalId: String
    ) = request {
        animalApi.getAllAnimalBreeds(animalId)
    }
}