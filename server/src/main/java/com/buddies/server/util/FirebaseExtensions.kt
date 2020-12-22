package com.buddies.server.util

import com.buddies.common.model.Animal
import com.buddies.common.model.AnimalInfo
import com.buddies.common.model.Breed
import com.buddies.common.model.BreedInfo
import com.buddies.common.model.DocumentTransformationException
import com.buddies.common.model.EncryptionKey
import com.buddies.common.model.EncryptionKeyInfo
import com.buddies.common.model.Owner
import com.buddies.common.model.Ownership
import com.buddies.common.model.OwnershipInfo
import com.buddies.common.model.Pet
import com.buddies.common.model.PetInfo
import com.buddies.common.model.Tag
import com.buddies.common.model.TagInfo
import com.buddies.common.model.User
import com.buddies.common.model.UserInfo
import com.buddies.common.util.toOwnershipCategory
import com.buddies.server.model.Notification
import com.buddies.server.model.NotificationInfo
import com.buddies.server.model.StoragePicture
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.ListResult
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

fun DocumentSnapshot.toEncryptionKey() =
    EncryptionKey(id, to(EncryptionKeyInfo::class))

fun DocumentSnapshot.toTag() =
    Tag(id, to(TagInfo::class))

fun QuerySnapshot.toTag() = documents[0].run {
    Tag(id, to(TagInfo::class))
}

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

fun ListResult.toStoragePictures() = items.map {
    StoragePicture(it.name, it.downloadUrl)
}

fun UploadTask.TaskSnapshot.getDownloadUrl() = storage.downloadUrl