package com.buddies.missing_feed.usecase

import com.buddies.common.model.MissingPet
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.MissingPetApi

class MissingFeedUseCases(
    private val missingPetApi: MissingPetApi,
) : BaseUseCases() {

    suspend fun getCurrentUser() = request {
        missingPetApi.getCurrentUser()
    }

    suspend fun getCurrentUserPets() = request {
        missingPetApi.getCurrentUserMissingPets(PETS_PREVIEW_LIST_SIZE)
    }?.map {
        it.mapAnimal()
    }

    suspend fun getMostRecentPets() = request {
        missingPetApi.getRecentMissingPets(PETS_PREVIEW_LIST_SIZE)
    }?.map {
        it.mapAnimal()
    }

    suspend fun getNearestPets(location: Pair<Double?, Double?>) = request {
        missingPetApi.getNearMissingPets(location, PETS_PREVIEW_LIST_SIZE)
    }?.map {
        it.mapAnimal()
    }

    private suspend fun getAnimalById(
        animalId: String
    ) = request {
        missingPetApi.getAnimal(animalId)
    }

    private suspend fun MissingPet.mapAnimal() =
        copy(info = info.copy(
            animal = getAnimalById(info.animal)?.animalInfo?.name ?: "")
        )

    companion object {
        private const val PETS_PREVIEW_LIST_SIZE = 10
    }
}