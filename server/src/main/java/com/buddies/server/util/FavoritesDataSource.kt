package com.buddies.server.util

import com.buddies.common.model.Favorite
import com.buddies.common.model.Result
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class FavoritesDataSource(
    private val request: suspend (Int, DocumentSnapshot?) -> Result<QuerySnapshot>
) : BaseDataSource<Favorite>() {

    override suspend fun request(
        size: Int,
        key: DocumentSnapshot?
    ): Pair<QuerySnapshot?, List<Favorite>?> {

        val response = request.invoke(size, key)
            .handleResult()

        val favorites = response?.toFavorites()

        return response to favorites
    }
}