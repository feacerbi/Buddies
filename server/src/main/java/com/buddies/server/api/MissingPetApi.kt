package com.buddies.server.api

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.buddies.common.model.DefaultError
import com.buddies.common.model.DefaultErrorException
import com.buddies.common.model.ErrorCode
import com.buddies.common.model.InfoType
import com.buddies.common.model.MissingPet
import com.buddies.common.model.MissingPetInfo
import com.buddies.common.model.NewMissingPet
import com.buddies.common.util.Sorting
import com.buddies.common.util.generateNewId
import com.buddies.common.util.handleAccessResult
import com.buddies.common.util.handleResult
import com.buddies.server.repository.AnimalsRepository
import com.buddies.server.repository.BreedsRepository
import com.buddies.server.repository.GalleryRepository
import com.buddies.server.repository.MissingPetsRepository
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.BaseDataSource.Companion.DEFAULT_PAGE_SIZE
import com.buddies.server.util.MissingPetsDataSource
import com.buddies.server.util.getDownloadUrl
import com.buddies.server.util.keysToString
import com.buddies.server.util.toAnimal
import com.buddies.server.util.toBreed
import com.buddies.server.util.toMissingPet
import com.buddies.server.util.toMissingPets
import com.buddies.server.util.toUser
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

class MissingPetApi(
    private val usersRepository: UsersRepository,
    private val missingPetsRepository: MissingPetsRepository,
    private val animalsRepository: AnimalsRepository,
    private val breedsRepository: BreedsRepository,
    private val galleryRepository: GalleryRepository
): BaseApi() {

    suspend fun getCurrentUser() = runWithResult {
        usersRepository.getCurrentUser()
            .handleTaskResult()
            .toUser()
    }

    suspend fun addMissingPet(
        newPet: NewMissingPet
    ) = addMissingPet(newPet, usersRepository.getCurrentUserId())

    suspend fun getUser(
        userId: String
    ) = runWithResult {
        usersRepository.getUser(userId)
            .handleTaskResult()
            .toUser()
    }

    suspend fun getPet(
        petId: String
    ) = runWithResult {
        missingPetsRepository.getPet(petId)
            .handleTaskResult()
            .toMissingPet()
    }

    suspend fun getAnimalAndBreed(
        animalId: String,
        breedId: String
    ) = runWithResult {
        Pair(
            animalsRepository.getAnimal(animalId)
                .handleTaskResult()
                .toAnimal(),
            breedsRepository.getBreed(breedId)
                .handleTaskResult()
                .toBreed()
        )
    }

    suspend fun updateName(
        petId: String,
        name: String
    ) = runWithResult {
        checkAccess(petId)

        runTransactions(
            missingPetsRepository.updateName(petId, name)
        )
    }

    suspend fun updateAnimal(
        petId: String,
        animalId: String,
        breedId: String
    ) = runWithResult {
        checkAccess(petId)

        runTransactions(
            missingPetsRepository.updateAnimal(petId, animalId),
            missingPetsRepository.updateBreed(petId, breedId)
        )
    }

    suspend fun updatePhoto(
        petId: String,
        photo: Uri
    ) = runWithResult {
        checkAccess(petId)

        val uploadResult = missingPetsRepository.uploadProfileImage(petId, photo)
            .handleTaskResult()

        val downloadUri = uploadResult.getDownloadUrl()
            .handleTaskResult()

        runTransactions(
            missingPetsRepository.updatePhoto(petId, downloadUri.toString())
        )
    }

    suspend fun updateContactInfo(
        petId: String,
        contactInfo: Map<InfoType, String>
    ) = runWithResult {
        checkAccess(petId)

        runTransactions(
            missingPetsRepository.updateReporterInfo(petId, contactInfo.keysToString())
        )
    }

    suspend fun updateLocation(
        petId: String,
        latitude: Double,
        longitude: Double
    ) = runWithResult {
        checkAccess(petId)

        runTransactions(
            missingPetsRepository.updateLatitude(petId, latitude),
            missingPetsRepository.updateLongitude(petId, longitude)
        )
    }

    suspend fun markAsReturned(
        petId: String
    ) = runWithResult {
        checkAccess(petId)

        runTransactions(
            missingPetsRepository.updateReturned(petId)
        )
    }

    suspend fun getMissingPetsFlowWithPaging(
        query: String?,
        sorting: Sorting,
        missingType: String,
        pageSize: Int = -1
    ): Flow<PagingData<MissingPet>> {

        val missingPetsDataSource = MissingPetsDataSource(
            usersRepository.getCurrentUserId(),
            query,
            sorting,
            missingType,
            ::getMissingPetsWithPaging
        )

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
            .filter { it.info.returned == false }
    }

    suspend fun getRecentMissingPets(
        limit: Int
    ) = runWithResult {
        missingPetsRepository.getPetsOrderedByTime(limit.toLong())
            .handleTaskResult()
            .toMissingPets()
            .filter { it.info.reporter != usersRepository.getCurrentUserId() }
            .filter { it.info.returned == false }
    }

    suspend fun getNearMissingPets(
        location: Pair<Double?, Double?>,
        limit: Int,
        maxDistance: Int = 1
    ) = runWithResult {
        val latitude = location.first
        val longitude = location.second

        if (latitude != null && longitude != null) {
            val maxLatitude = latitude + maxDistance
            val minLatitude = latitude - maxDistance
            val maxLongitude = longitude + maxDistance
            val minLongitude = longitude - maxDistance

            val petsByLatitude = missingPetsRepository.getPetsOrderedByLatitude(maxLatitude, minLatitude)
                .handleTaskResult()
                .toMissingPets()

            val petsByLongitude = missingPetsRepository.getPetsOrderedByLongitude(maxLongitude, minLongitude)
                .handleTaskResult()
                .toMissingPets()

            petsByLatitude
                .filter { petByLat -> petsByLongitude.any { petByLon -> petByLat.id == petByLon.id } }
                .filter { it.info.reporter != usersRepository.getCurrentUserId() }
                .filter { it.info.returned == false }
                .take(limit)
        } else {
            emptyList()
        }
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
        query: String?,
        sorting: Sorting,
        missingType: String,
        start: DocumentSnapshot? = null
    ) = runWithResult {
        missingPetsRepository.getPetsWithPaging(pageSize.toLong(), query, sorting, missingType, start)
            .handleTaskResult()
    }

    private suspend fun addMissingPet(
        newMissingPet: NewMissingPet,
        userId: String
    ) = runWithResult {
        val newPetId = generateNewId()

        val type = newMissingPet.type
        val photo = newMissingPet.photo
        val name = newMissingPet.name
        val animal = newMissingPet.animal
        val breed = newMissingPet.breed
        val contactInfo = newMissingPet.contactInfo

        if (name == null || animal == null || breed == null || contactInfo == null) {
            throw DefaultErrorException(DefaultError(ErrorCode.RESULT_NULL))
        }

        var downloadUri = ""

        if (photo != null) {
            val uploadResult = missingPetsRepository.uploadProfileImage(newPetId, photo)
                .handleTaskResult()

            downloadUri = uploadResult.getDownloadUrl()
                .handleTaskResult()
                .toString()
        }

        val petInfo = MissingPetInfo(
            type.name,
            name,
            downloadUri,
            animal.id,
            breed.id,
            newMissingPet.description,
            userId,
            contactInfo.keysToString(),
            newMissingPet.latitude,
            newMissingPet.longitude,
        )

        runTransactions(
            missingPetsRepository.addPet(newPetId, petInfo),
        )
    }

    suspend fun removeMissingPet(
        petId: String
    ) = runWithResult {
        checkAccess(petId)

        galleryRepository.deleteGallery(petId)

        runTransactions(
            missingPetsRepository.deletePet(petId)
        )
    }

    private suspend fun checkAccess(
        petId: String
    ) = runWithResult {
        val user = getCurrentUser()
            .handleResult()
        val pet = getPet(petId)
            .handleResult()

        user != null && pet != null && pet.info.reporter == user.id
    }.handleAccessResult()
}