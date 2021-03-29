package com.buddies.server.repository

import android.net.Uri
import com.buddies.common.model.PetInfo
import com.buddies.common.util.generateNewId
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class PetsRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    fun addPet(
        newPetId: String,
        info: PetInfo
    ): Transaction.() -> Unit = {
        set(
            db.collection(PETS_COLLECTION).document(newPetId),
            info
        )
    }

    fun deletePet(
        petId: String
    ): Transaction.() -> Unit = {
        delete(
            db.collection(PETS_COLLECTION).document(petId)
        )
    }

    fun getPet(
        petId: String
    ): Task<DocumentSnapshot> =
        db.collection(PETS_COLLECTION).document(petId)
            .get()

    fun getPetByTag(
        tagId: String
    ): Task<QuerySnapshot> =
        db.collection(PETS_COLLECTION)
            .whereEqualTo(TAG_FIELD, tagId)
            .get()

    fun updateName(
        petId: String,
        name: String
    ) = updatePetField(petId, NAME_FIELD, name)

    fun updateTag(
        petId: String,
        tag: String
    ) = updatePetField(petId, TAG_FIELD, tag)

    fun updateAnimal(
        petId: String,
        animal: String
    ) = updatePetField(petId, ANIMAL_FIELD, animal)

    fun updateBreed(
        petId: String,
        breed: String
    ) = updatePetField(petId, BREED_FIELD, breed)

    fun updatePhoto(
        petId: String,
        photo: String
    ) = updatePetField(petId, PHOTO_FIELD, photo)

    fun updateLost(
        petId: String,
        lost: Boolean
    ) = updatePetField(petId, LOST_FIELD, lost)

    private fun updatePetField(
        petId: String,
        petField: String,
        value: String
    ): Transaction.() -> Unit = {
        update(
            db.collection(PETS_COLLECTION).document(petId),
            petField,
            value
        )
    }

    private fun updatePetField(
        petId: String,
        petField: String,
        value: Boolean
    ): Transaction.() -> Unit = {
        update(
            db.collection(PETS_COLLECTION).document(petId),
            petField,
            value
        )
    }

    fun uploadProfileImage(
        petId: String,
        photoUri: Uri
    ): UploadTask =
        storage.getReference(PETS_PATH)
            .child(petId)
            .child(PROFILE_PATH)
            .child(PROFILE_PICTURE_NAME)
            .putFile(photoUri)

    fun uploadGalleryImage(
        petId: String,
        photoUri: Uri
    ): UploadTask =
        storage.getReference(PETS_PATH)
            .child(petId)
            .child(GALLERY_PATH)
            .child(generateNewId())
            .putFile(photoUri)

    fun listGalleryPictures(
        petId: String
    ): Task<ListResult> =
        storage.getReference(PETS_PATH)
            .child(petId)
            .child(GALLERY_PATH)
            .listAll()

    fun deleteGalleryImage(
        petId: String,
        photoId: String
    ): Task<Void> =
        storage.getReference(PETS_PATH)
            .child(petId)
            .child(GALLERY_PATH)
            .child(photoId)
            .delete()

    fun deleteGallery(
        petId: String
    ): Task<Void> =
        storage.getReference(PETS_PATH)
            .child(petId)
            .delete()

    companion object {
        private const val PETS_COLLECTION = "pets"
        private const val NAME_FIELD = "name"
        private const val TAG_FIELD = "tag"
        private const val ANIMAL_FIELD = "animal"
        private const val BREED_FIELD = "breed"
        private const val PHOTO_FIELD = "photo"
        private const val LOST_FIELD = "lost"

        private const val PETS_PATH = "pets"
        private const val PROFILE_PATH = "profile"
        private const val PROFILE_PICTURE_NAME = "profile_picture"
        private const val GALLERY_PATH = "gallery"
    }
}