package com.buddies.server.util

import com.buddies.common.model.MissingPet
import com.buddies.common.model.Result
import com.buddies.common.util.Sorting
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class MissingPetsDataSource(
    private val reporter: String,
    private val query: String?,
    private val sorting: Sorting,
    private val request: suspend (Int, String?, Sorting, DocumentSnapshot?) -> Result<QuerySnapshot>
) : BaseDataSource<MissingPet>() {

    override suspend fun request(
        size: Int,
        key: DocumentSnapshot?
    ): Pair<QuerySnapshot?, List<MissingPet>?> {

        val response = request.invoke(size, query, sorting, key)
            .handleResult()

        val pets = response?.toMissingPets()
            ?.filter { it.info.reporter != reporter }
            ?.filter { it.info.returned == false }

        return response to pets
    }
}