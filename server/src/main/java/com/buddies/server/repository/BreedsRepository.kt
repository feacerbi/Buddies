package com.buddies.server.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BreedsRepository {

    private val db = Firebase.firestore

    fun getBreedsList(): Task<QuerySnapshot> =
        db.collection(BREEDS_COLLECTION).get()

    fun getBreed(
        breedId: String
    ): Task<DocumentSnapshot> =
        db.collection(BREEDS_COLLECTION).document(breedId)
            .get()

    fun getBreedsFromAnimal(
        animalId: String
    ): Task<QuerySnapshot> =
        db.collection(BREEDS_COLLECTION)
            .whereEqualTo(ANIMAL_FIELD, animalId)
            .get()

    companion object {
        private const val BREEDS_COLLECTION = "breeds"
        private const val ANIMAL_FIELD = "animal"
    }
}