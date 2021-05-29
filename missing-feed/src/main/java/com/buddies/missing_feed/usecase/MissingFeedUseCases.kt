package com.buddies.missing_feed.usecase

import com.buddies.common.model.MissingPet
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.MissingPetApi
import com.buddies.settings.repository.KeyValueRepository
import com.buddies.settings.repository.KeyValueRepository.StringKey.LOCATION_RADIUS

class MissingFeedUseCases(
    private val missingPetApi: MissingPetApi,
    private val keyValueRepository: KeyValueRepository
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
        val maxRadius = keyValueRepository.getStringValue(LOCATION_RADIUS)?.toIntOrNull() ?: DEFAULT_MAX_RADIUS
        missingPetApi.getNearMissingPets(location, PETS_PREVIEW_LIST_SIZE, maxRadius)
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
        private const val DEFAULT_MAX_RADIUS = 1
    }
}