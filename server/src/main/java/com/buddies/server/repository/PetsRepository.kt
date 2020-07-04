package com.buddies.server.repository

import com.buddies.common.model.Pet
import com.buddies.common.model.PetInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
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

    fun editPet(
        pet: Pet
    ): Transaction.() -> Unit = {
        set(
            db.collection(PETS_COLLECTION).document(pet.id),
            pet.info,
            SetOptions.merge()
        )
    }

    fun getPet(
        petId: String
    ): Task<DocumentSnapshot> =
        db.collection(PETS_COLLECTION).document(petId)
            .get()

    companion object {
        private const val PETS_COLLECTION = "pets"
    }
}