package com.buddies.server.util

import com.buddies.common.model.Owner
import com.buddies.common.model.Ownership
import com.buddies.common.model.OwnershipCategory.VISITOR
import com.buddies.common.model.Result
import com.buddies.common.util.toOwnershipCategory
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class OwnersDataSource(
    private val ownerships: List<Ownership>?,
    private val query: String,
    private val request: suspend (Int, String, DocumentSnapshot?) -> Result<QuerySnapshot>
) : BaseDataSource<Owner>() {

    override suspend fun request(
        size: Int,
        key: DocumentSnapshot?
    ): Pair<QuerySnapshot?, List<Owner>?> {

        val response = request.invoke(size, query, key)
            .handleResult()

        val owners = response?.toUsers()?.map { user ->
            Owner(
                user,
                ownerships?.firstOrNull { it.info.userId == user.id }?.info?.category?.toOwnershipCategory() ?: VISITOR)
        }

        return response to owners
    }
}