package com.buddies.server.repository

import com.buddies.common.model.NotificationInfo
import com.buddies.common.util.generateNewId
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotificationsRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private val userAuth = auth.currentUser

    private fun getCurrentUserId() = userAuth?.uid ?: ""

    fun getCurrentUserNotifications() = getUserNotifications(getCurrentUserId())

    fun getNotification(
        notificationId: String
    ): Task<DocumentSnapshot> =
        db.collection(NOTIFICATIONS_COLLECTION).document(notificationId)
            .get()

    fun getUserNotifications(
        userId: String
    ): Task<QuerySnapshot> =
        db.collection(NOTIFICATIONS_COLLECTION)
            .whereEqualTo(USERID_FIELD, userId)
            .get()

    fun queryCurrentUserNotifications(
    ) = queryUserNotifications(getCurrentUserId())

    fun queryUserNotifications(
        userId: String
    ): Query =
        db.collection(NOTIFICATIONS_COLLECTION)
            .whereEqualTo(USERID_FIELD, userId)

    fun addNotification(
        info: NotificationInfo
    ): Transaction.() -> Unit = {
        set(
            db.collection(NOTIFICATIONS_COLLECTION).document(generateNewId()),
            info
        )
    }

    fun removeNotification(
        notificationId: String
    ): Transaction.() -> Unit = {
        delete(
            db.collection(NOTIFICATIONS_COLLECTION).document(notificationId)
        )
    }

    fun markAsRead(
        notificationId: String
    ): Transaction.() -> Unit = {
        update(
            db.collection(NOTIFICATIONS_COLLECTION).document(notificationId),
            UNREAD_FIELD,
            false
        )
    }

    companion object {
        private const val NOTIFICATIONS_COLLECTION = "notifications"
        private const val USERID_FIELD = "userId"
        private const val UNREAD_FIELD = "unread"
    }
}