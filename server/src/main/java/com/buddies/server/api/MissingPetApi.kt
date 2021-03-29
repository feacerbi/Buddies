package com.buddies.server.api

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.buddies.common.model.MissingPet
import com.buddies.server.repository.AnimalsRepository
import com.buddies.server.repository.MissingPetsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.BaseDataSource.Companion.DEFAULT_PAGE_SIZE
import com.buddies.server.util.MissingPetsDataSource
import com.buddies.server.util.toAnimal
import com.buddies.server.util.toMissingPets
import com.buddies.server.util.toUser
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

class MissingPetApi(
    private val usersRepository: UsersRepository,
    private val missingPetsRepository: MissingPetsRepository,
    private val animalsRepository: AnimalsRepository
): BaseApi() {

    suspend fun getCurrentUser() = runWithResult {
        usersRepository.getCurrentUser()
            .handleTaskResult()
            .toUser()
    }

    suspend fun getMissingPetsFlowWithPaging(
        pageSize: Int = -1
    ): Flow<PagingData<MissingPet>> {

        val missingPetsDataSource = MissingPetsDataSource(::getMissingPetsWithPaging)

        return Pager(PagingConfig(if (pageSize != -1) pageSize else DEFAULT_PAGE_SIZE)) {
            missingPetsDataSource
        }.flow
    }

    suspend fun getCurrentUserMissingPets(
        limit: Int
    ) = runWithResult {
        missingPetsRepository.getUserPets(usersRepository.getCurrentUserId(), limit.toLong())
            .handleTaskResult()
            .toMissingPets()
    }

    suspend fun getRecentMissingPets(
        limit: Int
    ) = runWithResult {
        missingPetsRepository.getPetsOrderedByTime(limit.toLong())
            .handleTaskResult()
            .toMissingPets()
    }

    suspend fun getNearMissingPets(
        limit: Int
    ) = runWithResult {
        missingPetsRepository.getPetsOrderedByLocation(limit.toLong())
            .handleTaskResult()
            .toMissingPets()
    }

    suspend fun getAnimal(
        animalId: String
    ) = runWithResult {
        animalsRepository.getAnimal(animalId)
            .handleTaskResult()
            .toAnimal()
    }

    private suspend fun getMissingPetsWithPaging(
        pageSize: Int,
        start: DocumentSnapshot? = null
    ) = runWithResult {
        missingPetsRepository.getPetsWithPaging(pageSize, start)
            .handleTaskResult()
    }
}