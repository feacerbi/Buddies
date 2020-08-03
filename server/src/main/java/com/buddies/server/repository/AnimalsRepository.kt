package com.buddies.server.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AnimalsRepository {

    private val db = Firebase.firestore

    fun getAnimalsList(): Task<QuerySnapshot> =
        db.collection(ANIMALS_COLLECTION).get()

    fun getAnimal(
        animalId: String
    ): Task<DocumentSnapshot> =
        db.collection(ANIMALS_COLLECTION).document(animalId)
            .get()

    companion object {
        private const val ANIMALS_COLLECTION = "animals"
    }
}