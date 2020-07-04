package com.buddies.server.util

import com.buddies.common.model.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlin.reflect.KClass

fun <T : Any> DocumentSnapshot.to(clazz: KClass<T>) =
    toObject(clazz.java) ?: throw DocumentTransformationException(this)

fun DocumentSnapshot.toUser() =
    User(id, to(UserInfo::class))

fun DocumentSnapshot.toPet() =
    Pet(id, to(PetInfo::class))

fun QuerySnapshot.toOwnerships() = documents.map { doc ->
    Ownership(doc.id, doc.to(OwnershipInfo::class))
}