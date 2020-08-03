package com.buddies.server.repository

import com.buddies.common.model.OwnershipInfo
import com.buddies.common.util.generateNewId
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OwnershipsRepository {

    private val db = Firebase.firestore

    fun addOwnership(
        info: OwnershipInfo
    ): Transaction.() -> Unit = {
        set(
            db.collection(OWNERSHIPS_COLLECTION).document(generateNewId()),
            info
        )
    }

    fun removeOwnership(
        ownershipId: String
    ): Transaction.() -> Unit = {
        delete(
            db.collection(OWNERSHIPS_COLLECTION).document(ownershipId)
        )
    }

    fun updateOwnership(
        ownershipId: String,
        category: String
    ): Transaction.() -> Unit = {
        update(
            db.collection(OWNERSHIPS_COLLECTION).document(ownershipId),
            OWNERSHIP_CATEGORY_FIELD,
            category
        )
    }

    fun getUserOwnerships(
        userId: String
    ): Task<QuerySnapshot> =
        db.collection(OWNERSHIPS_COLLECTION)
            .whereEqualTo(OWNERSHIP_USERID_FIELD, userId)
            .get()

    fun getPetOwnerships(
        petId: String
    ): Task<QuerySnapshot> =
        db.collection(OWNERSHIPS_COLLECTION)
            .whereEqualTo(OWNERSHIP_PETID_FIELD, petId)
            .get()

    fun getOwnership(
        petId: String,
        userId: String
    ): Task<QuerySnapshot> =
        db.collection(OWNERSHIPS_COLLECTION)
            .whereEqualTo(OWNERSHIP_PETID_FIELD, petId)
            .whereEqualTo(OWNERSHIP_USERID_FIELD, userId)
            .get()

    companion object {
        private const val OWNERSHIPS_COLLECTION = "ownerships"
        private const val OWNERSHIP_USERID_FIELD = "userId"
        private const val OWNERSHIP_PETID_FIELD = "petId"
        private const val OWNERSHIP_CATEGORY_FIELD = "category"
    }
}