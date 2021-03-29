package com.buddies.server.repository

import android.net.Uri
import com.buddies.common.model.MissingPetInfo
import com.buddies.common.util.generateNewId
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class MissingPetsRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    fun addPet(
        newPetId: String,
        info: MissingPetInfo
    ): Transaction.() -> Unit = {
        set(
            db.collection(MISSING_PETS_COLLECTION).document(newPetId),
            info
        )
    }

    fun deletePet(
        petId: String
    ): Transaction.() -> Unit = {
        delete(
            db.collection(MISSING_PETS_COLLECTION).document(petId)
        )
    }

    fun getPet(
        petId: String
    ): Task<DocumentSnapshot> =
        db.collection(MISSING_PETS_COLLECTION).document(petId)
            .get()

    fun getPetsWithPaging(
        pageSize: Int,
        start: DocumentSnapshot? = null
    ): Task<QuerySnapshot> {
        val pets = db.collection(MISSING_PETS_COLLECTION)
            .orderBy(NAME_FIELD)
            .limit(pageSize.toLong())

        val pageQuery = if (start != null) pets.startAfter(start) else pets

        return pageQuery.get()
    }

    fun getUserPets(
        userId: String,
        limit: Long
    ): Task<QuerySnapshot> =
        db.collection(MISSING_PETS_COLLECTION)
            .whereEqualTo(REPORTER_FIELD, userId)
            .orderBy(NAME_FIELD)
            .limit(limit)
            .get()

    fun getPetsOrderedByTime(
        limit: Long
    ): Task<QuerySnapshot> =
        db.collection(MISSING_PETS_COLLECTION)
            .orderBy(CREATED_FIELD, Query.Direction.DESCENDING)
            .limit(limit)
            .get()

    fun getPetsOrderedByLocation(
        limit: Long
    ): Task<QuerySnapshot> =
        db.collection(MISSING_PETS_COLLECTION)
            .orderBy(LOCATION_FIELD)
            .limit(limit)
            .get()

    fun updateName(
        petId: String,
        name: String
    ) = updatePetField(petId, NAME_FIELD, name)

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

    private fun updatePetField(
        petId: String,
        petField: String,
        value: String
    ): Transaction.() -> Unit = {
        update(
            db.collection(MISSING_PETS_COLLECTION).document(petId),
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
            db.collection(MISSING_PETS_COLLECTION).document(petId),
            petField,
            value
        )
    }

    fun uploadProfileImage(
        petId: String,
        photoUri: Uri
    ): UploadTask =
        storage.getReference(MISSING_PETS_PATH)
            .child(petId)
            .child(PROFILE_PATH)
            .child(PROFILE_PICTURE_NAME)
            .putFile(photoUri)

    fun uploadGalleryImage(
        petId: String,
        photoUri: Uri
    ): UploadTask =
        storage.getReference(MISSING_PETS_PATH)
            .child(petId)
            .child(GALLERY_PATH)
            .child(generateNewId())
            .putFile(photoUri)

    fun listGalleryPictures(
        petId: String
    ): Task<ListResult> =
        storage.getReference(MISSING_PETS_PATH)
            .child(petId)
            .child(GALLERY_PATH)
            .listAll()

    fun deleteGalleryImage(
        petId: String,
        photoId: String
    ): Task<Void> =
        storage.getReference(MISSING_PETS_PATH)
            .child(petId)
            .child(GALLERY_PATH)
            .child(photoId)
            .delete()

    fun deleteGallery(
        petId: String
    ): Task<Void> =
        storage.getReference(MISSING_PETS_PATH)
            .child(petId)
            .delete()

    companion object {
        private const val MISSING_PETS_COLLECTION = "missing-pets"
        private const val NAME_FIELD = "name"
        private const val ANIMAL_FIELD = "animal"
        private const val BREED_FIELD = "breed"
        private const val PHOTO_FIELD = "photo"
        private const val REPORTER_FIELD = "reporter"
        private const val LOCATION_FIELD = "location"
        private const val CREATED_FIELD = "created"

        private const val MISSING_PETS_PATH = "missing-pets"
        private const val PROFILE_PATH = "profile"
        private const val PROFILE_PICTURE_NAME = "profile_picture"
        private const val GALLERY_PATH = "gallery"
    }
}