package com.buddies.server.util

import com.buddies.common.model.MissingPet
import com.buddies.common.model.Result
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class MissingPetsDataSource(
    private val query: String?,
    private val request: suspend (Int, String?, DocumentSnapshot?) -> Result<QuerySnapshot>
) : BaseDataSource<MissingPet>() {

    override suspend fun request(
        size: Int,
        key: DocumentSnapshot?
    ): Pair<QuerySnapshot?, List<MissingPet>?> {

        val response = request.invoke(size, query, key)
            .handleResult()

        val pets = response?.toMissingPets()

        return response to pets
    }
}