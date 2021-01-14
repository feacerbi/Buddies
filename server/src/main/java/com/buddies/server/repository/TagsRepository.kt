package com.buddies.server.repository

import com.buddies.common.model.TagInfo
import com.buddies.common.util.generateNewId
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TagsRepository {

    private val db = Firebase.firestore

    fun getTagByValue(
        tagValue: String
    ): Task<QuerySnapshot> =
        db.collection(TAGS_COLLECTION)
            .whereEqualTo(VALUE_FIELD, tagValue)
            .get()

    fun getTagById(
        tagId: String
    ): Task<DocumentSnapshot> =
        db.collection(TAGS_COLLECTION).document(tagId)
            .get()

    fun addTag(
        tagInfo: TagInfo
    ): Transaction.() -> Unit = {
        set(
            db.collection(TAGS_COLLECTION).document(generateNewId()),
            tagInfo
        )
    }

    fun markTagAvailable(
        tagId: String
    ): Transaction.() -> Unit = {
        update(
            db.collection(TAGS_COLLECTION).document(tagId),
            AVAILABLE_FIELD,
            true
        )
    }

    fun markTagUnavailable(
        tagId: String
    ): Transaction.() -> Unit = {
        update(
            db.collection(TAGS_COLLECTION).document(tagId),
            AVAILABLE_FIELD,
            false
        )
    }

    companion object {
        private const val TAGS_COLLECTION = "tags"
        private const val VALUE_FIELD = "value"
        private const val AVAILABLE_FIELD = "available"
    }
}