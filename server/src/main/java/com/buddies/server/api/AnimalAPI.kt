package com.buddies.server.api

import com.buddies.server.repository.AnimalsRepository
import com.buddies.server.repository.BreedsRepository
import com.buddies.server.util.toAnimals
import com.buddies.server.util.toBreeds

class AnimalAPI(
    private val animalsRepository: AnimalsRepository,
    private val breedsRepository: BreedsRepository
) : BaseApi() {

    suspend fun getAllAnimals() = runWithResult {
        animalsRepository.getAnimalsList()
            .handleTaskResult()
            .toAnimals()
    }

    suspend fun getAllAnimalBreeds(
        animalId: String
    ) = runWithResult {
        breedsRepository.getBreedsFromAnimal(animalId)
            .handleTaskResult()
            .toBreeds()
    }
}