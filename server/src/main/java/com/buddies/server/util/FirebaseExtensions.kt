package com.buddies.server.util

import com.buddies.common.model.*
import com.buddies.common.util.toOwnershipCategory
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import kotlin.reflect.KClass

fun <T : Any> DocumentSnapshot.to(clazz: KClass<T>) =
    toObject(clazz.java) ?: throw DocumentTransformationException(this)

fun DocumentSnapshot.toUser() =
    User(id, to(UserInfo::class))

fun DocumentSnapshot.toPet() =
    Pet(id, to(PetInfo::class))

fun DocumentSnapshot.toAnimal() =
    Animal(id, to(AnimalInfo::class))

fun DocumentSnapshot.toBreed() =
    Breed(id, to(BreedInfo::class))

fun DocumentSnapshot.toOwner(ownership: Ownership) =
    Owner(toUser(), ownership.info.category.toOwnershipCategory())

fun QuerySnapshot.toOwnerships() = documents.map { doc ->
    Ownership(doc.id, doc.to(OwnershipInfo::class))
}

fun UploadTask.TaskSnapshot.getDownloadUrl() = storage.downloadUrl