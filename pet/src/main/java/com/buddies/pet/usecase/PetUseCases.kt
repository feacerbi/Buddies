package com.buddies.pet.usecase

import android.net.Uri
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.buddies.common.model.OwnershipCategory
import com.buddies.common.model.User
import com.buddies.common.usecase.BaseUseCases
import com.buddies.pet.data.GalleryUploadWorksDAO
import com.buddies.pet.model.GalleryUploadWorkEntity
import com.buddies.pet.model.GalleryUploadWorkEntity.Companion.toGalleryUploadWorkEntity
import com.buddies.pet.worker.GalleryUploadWorker
import com.buddies.server.api.AnimalApi
import com.buddies.server.api.PetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PetUseCases(
    private val petApi: PetApi,
    private val animalApi: AnimalApi,
    private val galleryUploadWorksDAO: GalleryUploadWorksDAO,
    private val workManager: WorkManager
) : BaseUseCases() {

    suspend fun getPetsFromCurrentUser() = request {
        petApi.getPetsFromCurrentUser()
    }

    suspend fun getPetsFromUser(user: User) = request {
        petApi.getPetsFromUser(user.id)
    }

    suspend fun getOwnersFromPet(petId: String) = request {
        petApi.getPetOwners(petId)
    }

    suspend fun getCurrentUserPetOwnership(petId: String) = request {
        petApi.getCurrentUserPetOwnership(petId)
    }

    suspend fun getPet(
        petId: String
    ) = request {
        petApi.getPet(petId)
    }

    suspend fun getAnimalAndBreed(
        animalId: String,
        breedId: String
    ) = request {
        petApi.getAnimalAndBreed(animalId, breedId)
    }

    suspend fun updatePetName(
        petId: String,
        name: String
    ) = request {
        petApi.updateName(petId, name)
    }

    suspend fun updatePetTag(
        petId: String,
        tag: String
    ) = request {
        petApi.updateTag(petId, tag)
    }

    suspend fun updatePetAnimal(
        petId: String,
        animalId: String,
        breedId: String
    ) = request {
        petApi.updateAnimal(petId, animalId, breedId)
    }

    suspend fun updatePetPhoto(
        petId: String,
        photo: Uri
    ) = request {
        petApi.updatePhoto(petId, photo)
    }

    suspend fun updateOwnership(
        petId: String,
        userId: String,
        ownershipCategory: OwnershipCategory
    ) = request {
        petApi.updatePetOwnership(petId, userId, ownershipCategory.id)
    }

    suspend fun getAllAnimals() = request {
        animalApi.getAllAnimals()
    }

    suspend fun getBreedsFromAnimal(
        animalId: String
    ) = request {
        animalApi.getAllAnimalBreeds(animalId)
    }

    suspend fun getPetTag(
        tagId: String
    ) = request {
        petApi.getPetTag(tagId)
    }

    @ExperimentalCoroutinesApi
    suspend fun getOwnersToInvite(
        petId: String,
        query: String
    ) = petApi.getOwnersFlowWithPaging(petId, query)
        .flowOn(Dispatchers.IO)

    suspend fun inviteOwner(
        userId: String,
        petId: String,
        category: OwnershipCategory
    ) = request {
        petApi.inviteOwner(userId, petId, category.id)
    }

    suspend fun getGallery(
        petId: String
    ) = request {
        petApi.getPetGalleryPictures(petId)
    }

    suspend fun addToGallery(
        petId: String,
        picture: Uri
    ) = request {
        petApi.addPetGalleryPicture(petId, picture)
    }

    suspend fun addPicturesToGallery(
        petId: String,
        pictures: List<Uri>
    ) = withContext(Dispatchers.IO) {
        galleryUploadWorksDAO.insertWorks(
            pictures.map { it.toGalleryUploadWorkEntity(petId) }
        )

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequest.Builder(GalleryUploadWorker::class.java)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            GALLERY_UPLOAD_REQUEST_NAME,
            ExistingWorkPolicy.REPLACE,
            request)
    }

    suspend fun getGalleryUploadWorks() = withContext(Dispatchers.IO) {
        galleryUploadWorksDAO.getWorks()
    }

    suspend fun removeUploadWork(work: GalleryUploadWorkEntity) = withContext(Dispatchers.IO) {
        galleryUploadWorksDAO.deleteWork(work)
    }

    suspend fun clearUploadWorks() = withContext(Dispatchers.IO) {
        galleryUploadWorksDAO.deleteWorks(galleryUploadWorksDAO.getWorks())
    }

    companion object {
        const val GALLERY_UPLOAD_REQUEST_NAME = "gallery_upload"
    }
}