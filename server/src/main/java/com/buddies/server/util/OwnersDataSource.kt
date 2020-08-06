package com.buddies.server.util

import androidx.paging.PagingSource
import com.buddies.common.model.Owner
import com.buddies.common.model.Ownership
import com.buddies.common.model.OwnershipCategory.VISITOR
import com.buddies.common.model.Result
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import com.buddies.common.util.toDefaultError
import com.buddies.common.util.toException
import com.buddies.common.util.toOwnershipCategory
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class OwnersDataSource(
    private val ownerships: List<Ownership>?,
    private val query: String,
    private val request: suspend (Int, String, DocumentSnapshot?) -> Result<QuerySnapshot>
) : PagingSource<DocumentSnapshot, Owner>() {

    override val keyReuseSupported: Boolean
        get() = true

    override suspend fun load(
        params: LoadParams<DocumentSnapshot>
    ): LoadResult<DocumentSnapshot, Owner> = safeRequest(params) {

        val nextPageStartElement = params.key

        val response = request.invoke(params.loadSize, query, nextPageStartElement)
            .handleResult()

        val result = response?.toUsers()?.map { user ->
            Owner(user, ownerships?.firstOrNull { it.info.userId == user.id }?.info?.category?.toOwnershipCategory() ?: VISITOR)
        }

        LoadResult.Page(
                data = result ?: listOf(),
                prevKey = null,
                nextKey = generateNextKey(response)
            )
    }

    private fun generateNextKey(snapshot: QuerySnapshot?) =
        snapshot?.let {
            if (it.documents.size > 0) {
                it.documents[it.documents.size - 1]
            } else {
                null
            }
        }

    private suspend fun safeRequest(
        params: LoadParams<DocumentSnapshot>,
        block: suspend (params: LoadParams<DocumentSnapshot>) -> LoadResult<DocumentSnapshot, Owner>
    ): LoadResult<DocumentSnapshot, Owner> = try {
        block.invoke(params)
    } catch (e: Exception) {
        LoadResult.Error(e.toDefaultError().toException())
    }

    private fun <T> Result<T>.handleResult() =
        when (this) {
            is Success -> data
            is Fail -> throw error.toException()
        }
}