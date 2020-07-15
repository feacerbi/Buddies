package com.buddies.server.repository

import com.buddies.common.model.PetInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PetsRepository {

    private val db = Firebase.firestore

    fun addPet(
        newPetId: String,
        info: PetInfo
    ): Transaction.() -> Unit = {
        set(
            db.collection(PETS_COLLECTION).document(newPetId),
            info)
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

    companion object {
        private const val PETS_COLLECTION = "pets"
        private const val NAME_FIELD = "name"
        private const val TAG_FIELD = "tag"
        private const val ANIMAL_FIELD = "animal"
        private const val BREED_FIELD = "breed"
        private const val PHOTO_FIELD = "photo"
    }
}