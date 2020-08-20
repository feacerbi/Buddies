package com.buddies.server.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TagsRepository {

    private val db = Firebase.firestore

    fun getTag(
        tagValue: String
    ): Task<QuerySnapshot> =
        db.collection(TAGS_COLLECTION)
            .whereEqualTo(VALUE_FIELD, tagValue)
            .get()

    companion object {
        private const val TAGS_COLLECTION = "tags"
        private const val VALUE_FIELD = "value"
    }
}