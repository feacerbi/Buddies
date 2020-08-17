package com.buddies.server.repository

import com.buddies.server.BuildConfig
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SecurityRepository {

    private val db = Firebase.firestore

    fun getEncryptionKey(
    ): Task<DocumentSnapshot> =
        db.collection(SECURITY_COLLECTION)
            .document(BuildConfig.BUILD_TYPE)
            .get()

    companion object {
        private const val SECURITY_COLLECTION = "security"
    }
}