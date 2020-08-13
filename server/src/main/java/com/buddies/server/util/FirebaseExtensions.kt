package com.buddies.server.util

import com.buddies.common.model.*
import com.buddies.common.util.toOwnershipCategory
import com.buddies.server.model.Notification
import com.buddies.server.model.NotificationInfo
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

fun DocumentSnapshot.toNotification() =
    Notification(id, to(NotificationInfo::class))

fun DocumentSnapshot.toOwner(ownership: Ownership) =
    Owner(toUser(), ownership.info.category.toOwnershipCategory())

fun QuerySnapshot.toOwnerships() = documents.map { doc ->
    Ownership(doc.id, doc.to(OwnershipInfo::class))
}

fun QuerySnapshot.toAnimals() = documents.map { doc ->
    Animal(doc.id, doc.to(AnimalInfo::class))
}

fun QuerySnapshot.toBreeds() = documents.map { doc ->
    Breed(doc.id, doc.to(BreedInfo::class))
}

fun QuerySnapshot.toUsers() = documents.map { doc ->
    User(doc.id, doc.to(UserInfo::class))
}

fun QuerySnapshot.toNotifications() = documents.map { doc ->
    Notification(doc.id, doc.to(NotificationInfo::class))
}

fun UploadTask.TaskSnapshot.getDownloadUrl() = storage.downloadUrl