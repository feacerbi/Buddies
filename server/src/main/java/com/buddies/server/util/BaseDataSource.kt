package com.buddies.server.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.buddies.common.model.Result
import com.buddies.common.model.Result.Fail
import com.buddies.common.model.Result.Success
import com.buddies.common.util.toDefaultError
import com.buddies.common.util.toException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

abstract class BaseDataSource<T : Any> : PagingSource<DocumentSnapshot, T>() {

    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, T>): DocumentSnapshot? =
        state.pages.last().nextKey

    override suspend fun load(
        params: LoadParams<DocumentSnapshot>
    ): LoadResult<DocumentSnapshot, T> = safeRequest(params) {

        val nextPageStartElement = params.key

        val (snapshot, list) = request(params.loadSize, nextPageStartElement)

        LoadResult.Page(
                data = list ?: listOf(),
                prevKey = null,
                nextKey = generateNextKey(snapshot)
            )
    }

    abstract suspend fun request(size: Int, key: DocumentSnapshot?): Pair<QuerySnapshot?, List<T>?>

    protected fun <T> Result<T>.handleResult() =
        when (this) {
            is Success -> data
            is Fail -> throw error.toException()
        }

    private fun generateNextKey(snapshot: QuerySnapshot?) =
        snapshot?.documents?.lastOrNull()

    private suspend fun safeRequest(
        params: LoadParams<DocumentSnapshot>,
        block: suspend (params: LoadParams<DocumentSnapshot>) -> LoadResult<DocumentSnapshot, T>
    ): LoadResult<DocumentSnapshot, T> = try {
        block.invoke(params)
    } catch (e: Exception) {
        LoadResult.Error(e.toDefaultError().toException())
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }
}