package com.buddies.missing_all.usecase

import androidx.paging.map
import com.buddies.common.model.MissingPet
import com.buddies.common.model.MissingType.FOUND
import com.buddies.common.model.MissingType.LOST
import com.buddies.common.usecase.BaseUseCases
import com.buddies.common.util.Sorting
import com.buddies.server.api.MissingPetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class AllMissingPetsUseCases(
    private val missingPetApi: MissingPetApi,
) : BaseUseCases() {

    suspend fun getAllLostPetsWithPaging(query: String?, sorting: Sorting) =
        missingPetApi.getMissingPetsFlowWithPaging(query, sorting, LOST.name, PETS_PAGE_SIZE)
            .flowOn(Dispatchers.IO)
            .map {
                it.map { pet ->
                    pet.mapAnimal()
                }
            }

    suspend fun getAllFoundPetsWithPaging(query: String?, sorting: Sorting) =
        missingPetApi.getMissingPetsFlowWithPaging(query, sorting, FOUND.name, PETS_PAGE_SIZE)
            .flowOn(Dispatchers.IO)
            .map {
                it.map { pet ->
                    pet.mapAnimal()
                }
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
    }
}