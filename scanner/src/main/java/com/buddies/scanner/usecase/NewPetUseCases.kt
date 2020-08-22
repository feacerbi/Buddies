package com.buddies.scanner.usecase

import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.AnimalApi
import com.buddies.server.api.NewPetApi

class NewPetUseCases(
    private val newPetApi: NewPetApi,
    private val animalApi: AnimalApi
) : BaseUseCases() {

    suspend fun validateTag(
        tagValue: String
    ) = request {
        newPetApi.isTagValid(tagValue)
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