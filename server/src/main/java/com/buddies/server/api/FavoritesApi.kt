package com.buddies.server.api

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.buddies.common.model.Favorite
import com.buddies.common.model.FavoriteInfo
import com.buddies.server.repository.UsersRepository
import com.buddies.server.util.BaseDataSource.Companion.DEFAULT_PAGE_SIZE
import com.buddies.server.util.FavoritesDataSource
import com.buddies.server.util.toFavorites
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

class FavoritesApi(
    private val usersRepository: UsersRepository
) : BaseApi() {

    suspend fun listenForUserFavorites(
        pageSize: Int = -1
    ): Flow<PagingData<Favorite>> {

        val favoritesDataSource = FavoritesDataSource(::getUserFavoritesWithPaging)

        return Pager(PagingConfig(if (pageSize != -1) pageSize else DEFAULT_PAGE_SIZE)) {
            favoritesDataSource
        }.flow
    }

    private suspend fun getUserFavoritesWithPaging(
        pageSize: Int,
        start: DocumentSnapshot? = null
    ) = runWithResult {
        usersRepository.getUserFavorites(pageSize, start)
            .handleTaskResult()
    }

    suspend fun addFavoritePetToUser(
        favoriteInfo: FavoriteInfo
    ) = runTransactionsWithResult(
        usersRepository.addFavorite(favoriteInfo)
    )

    suspend fun removeFavoritePetFromUser(
        favoriteId: String
    ) = runTransactionsWithResult(
        usersRepository.removeFavorite(favoriteId)
    )

    suspend fun removeFavoritePetFromUserById(
        petId: String
    ) = runWithResult {

        val favorite = usersRepository.getFavorite(petId)
            .handleTaskResult()
            .toFavorites()
            .first()

        runTransactions(
            usersRepository.removeFavorite(favorite.id)
        )
    }

    suspend fun isPetFavorite(
        petId: String
    ) = runWithResult {

        val favorites = usersRepository.getFavorite(petId)
            .handleTaskResult()
            .toFavorites()

        favorites.isNotEmpty()
    }
}