package com.buddies.server.repository

import android.net.Uri
import com.buddies.common.model.User
import com.buddies.common.model.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UsersRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val userAuth = auth.currentUser

    fun getCurrentUserId() = userAuth?.uid ?: ""

    fun setUser(
        info: UserInfo
    ): Transaction.() -> Unit = {
        set(
            db.collection(USERS_COLLECTION).document(getCurrentUserId()),
            info
        )
    }

    fun deleteUser(
        user: User
    ): Transaction.() -> Unit = {
        delete(
            db.collection(USERS_COLLECTION).document(user.id)
        )
    }

    fun deleteCurrentUser(
    ): Task<Void>? = userAuth?.delete()

    fun getCurrentUser() = getUser(getCurrentUserId())

    fun getUser(
        id: String
    ): Task<DocumentSnapshot> =
        db.collection(USERS_COLLECTION).document(id)
            .get()

    fun logout() = auth.signOut()

    fun updateName(
        name: String
    ): Transaction.() -> Unit = {
        update(
            db.collection(USERS_COLLECTION).document(getCurrentUserId()),
            NAME_FIELD,
            name)
    }

    fun updatePhoto(
        photo: Uri
    ): Transaction.() -> Unit = {
        update(
            db.collection(USERS_COLLECTION).document(getCurrentUserId()),
            PHOTO_FIELD,
            photo)
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val NAME_FIELD = "name"
        private const val PHOTO_FIELD = "photo"
    }
}