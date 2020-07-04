package com.buddies.common.model

import com.google.firebase.firestore.DocumentSnapshot

class DocumentTransformationException(
    val documentSnapshot: DocumentSnapshot
) : Exception() {
}