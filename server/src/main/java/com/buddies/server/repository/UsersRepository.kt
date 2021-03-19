package com.buddies.server.repository

import android.net.Uri
import com.buddies.common.model.FavoriteInfo
import com.buddies.common.model.User
import com.buddies.common.model.UserInfo
import com.buddies.common.util.generateNewId
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class UsersRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    private fun getCurrentAuthUser() = auth.currentUser

    fun getCurrentUserId() = getCurrentAuthUser()?.uid ?: ""

    fun setUser(
        info: UserInfo
    ): Transaction.() -> Unit = {
        set(
            db.collection(USERS_COLLECTION).document(getCurrentUserId()),
            info
        )
    }

    fun deleteCurrentUser() = getCurrentAuthUser()?.delete()

    fun deleteUser(
        user: User
    ): Transaction.() -> Unit = {
        delete(
            db.collection(USERS_COLLECTION).document(user.id)
        )
    }

    fun getCurrentUser() = getUser(getCurrentUserId())

    fun getUser(
        userId: String
    ): Task<DocumentSnapshot> =
        db.collection(USERS_COLLECTION).document(userId)
            .get()

    fun getUsers(
        pageSize: Int,
        query: String,
        start: DocumentSnapshot? = null
    ): Task<QuerySnapshot> {
        val users = db.collection(USERS_COLLECTION)
            .whereGreaterThanOrEqualTo(NAME_FIELD, query)
            .whereLessThan(NAME_FIELD, generateEndQuery(query))
            .orderBy(NAME_FIELD)
            .limit(pageSize.toLong())

        val pageQuery = if (start != null) users.startAfter(start) else users

        return pageQuery.get()
    }

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
        photo: String
    ): Transaction.() -> Unit = {
        update(
            db.collection(USERS_COLLECTION).document(getCurrentUserId()),
            PHOTO_FIELD,
            photo)
    }

    fun getUserFavorites(
        pageSize: Int,
        start: DocumentSnapshot? = null
    ): Task<QuerySnapshot> {
        val favorites = db.collection(USERS_COLLECTION)
            .document(getCurrentUserId())
            .collection(FAVORITES_COLLECTION)
            .limit(pageSize.toLong())

        val pageQuery = if (start != null) favorites.startAfter(start) else favorites

        return pageQuery.get()
    }

    fun addFavorite(
        favoriteInfo: FavoriteInfo
    ): Transaction.() -> Unit = {
        set(
            db.collection(USERS_COLLECTION).document(getCurrentUserId())
                .collection(FAVORITES_COLLECTION)
                .document(generateNewId()),
            favoriteInfo)
    }

    fun removeFavorite(
        favoriteId: String
    ): Transaction.() -> Unit = {
        delete(
            db.collection(USERS_COLLECTION).document(getCurrentUserId())
                .collection(FAVORITES_COLLECTION)
                .document(favoriteId))
    }

    fun getFavorite(
        petId: String
    ): Task<QuerySnapshot> =
        db.collection(USERS_COLLECTION)
            .document(getCurrentUserId())
            .collection(FAVORITES_COLLECTION)
            .whereEqualTo(PET_ID_FIELD, petId)
            .get()

    fun uploadImage(
        photoUri: Uri
    ): UploadTask =
        storage.getReference(USERS_PATH)
            .child(getCurrentUserId())
            .child(PROFILE_PATH)
            .child(PROFILE_PICTURE_NAME)
            .putFile(photoUri)

    private fun generateEndQuery(
        startQuery: String
    ): String {
        var end = startQuery[startQuery.length - 1]
        val newEnding = ++end

        return startQuery.dropLast(1) + newEnding
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val NAME_FIELD = "name"
        private const val PHOTO_FIELD = "photo"

        private const val FAVORITES_COLLECTION = "favorites"
        private const val PET_ID_FIELD = "petId"

        private const val USERS_PATH = "users"
        private const val PROFILE_PATH = "profile"
        private const val PROFILE_PICTURE_NAME = "profile_picture"
    }
}