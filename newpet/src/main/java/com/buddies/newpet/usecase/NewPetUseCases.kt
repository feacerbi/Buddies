package com.buddies.newpet.usecase

import com.buddies.common.model.NewPet
import com.buddies.common.model.OwnershipCategory
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.AnimalApi
import com.buddies.server.api.NewPetApi

class NewPetUseCases(
    private val newPetApi: NewPetApi,
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

    suspend fun addNewPet(
        newPet: NewPet,
        category: OwnershipCategory
    ) = request {
        newPetApi.addNewPet(newPet, category.id)
    }
}