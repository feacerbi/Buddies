package com.buddies.missing.usecase

import androidx.paging.map
import com.buddies.common.model.MissingPet
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.MissingPetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class MissingFeedUseCases(
    private val missingPetApi: MissingPetApi,
) : BaseUseCases() {

    suspend fun getCurrentUser() = request {
        missingPetApi.getCurrentUser()
    }

    suspend fun getAllPetsWithPaging() =
        missingPetApi.getMissingPetsFlowWithPaging(PETS_PAGE_SIZE)
            .flowOn(Dispatchers.IO)
            .map {
                it.map { pet ->
                    pet.mapAnimal()
                }
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
        private const val PETS_PAGE_SIZE = 20
        private const val PETS_PREVIEW_LIST_SIZE = 10
    }
}