package com.buddies.missing_new.usecase

import com.buddies.common.model.NewMissingPet
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.AnimalApi
import com.buddies.server.api.MissingPetApi

class NewMissingPetUseCases(
    private val missingPetApi: MissingPetApi,
    private val animalApi: AnimalApi
) : BaseUseCases() {

    suspend fun getAllAnimals() = request {
        animalApi.getAllAnimals()
    }

    suspend fun getBreedsFromAnimal(
        animalId: String
    ) = request {
        animalApi.getAllAnimalBreeds(animalId)
    }

    suspend fun addNewMissingPet(
        newMissingPet: NewMissingPet
    ) = request {
        missingPetApi.addMissingPet(newMissingPet)
    }

    suspend fun getCurrentUser() = request {
        missingPetApi.getCurrentUser()
    }
}