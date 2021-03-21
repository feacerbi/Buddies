package com.buddies.profile.usecase

import android.net.Uri
import androidx.paging.map
import com.buddies.common.model.Favorite
import com.buddies.common.model.FavoriteInfo
import com.buddies.common.model.PetFavorite
import com.buddies.common.usecase.BaseUseCases
import com.buddies.server.api.FavoritesApi
import com.buddies.server.api.NotificationsApi
import com.buddies.server.api.PetApi
import com.buddies.server.api.ProfileApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ProfileUseCases(
    private val profileApi: ProfileApi,
    private val favoritesApi: FavoritesApi,
    private val petApi: PetApi,
    private val notificationsApi: NotificationsApi
) : BaseUseCases() {

    suspend fun updateName(name: String) = request {
        profileApi.updateName(name)
    }

    suspend fun updatePhoto(photo: Uri) = request {
        profileApi.updatePhoto(photo)
    }

    suspend fun getCurrentUser() = request {
        profileApi.getCurrentUser()
    }

    suspend fun getNotifications() = request {
        notificationsApi.getCurrentUserNotifications()
    }

    suspend fun ignoreInvitation(notificationId: String) = request {
        notificationsApi.removeNotification(notificationId)
    }

    suspend fun acceptInvitation(notificationId: String) = request {
        notificationsApi.acceptInvitation(notificationId)
    }

    suspend fun addFavorite(favorite: PetFavorite) = request {
        favoritesApi.addFavoritePetToUser(FavoriteInfo(favorite.pet.id))
    }

    suspend fun removeFavorite(favorite: PetFavorite) = request {
        favoritesApi.removeFavoritePetFromUser(favorite.favoriteId)
    }

    @ExperimentalCoroutinesApi
    suspend fun listenForUserFavorites() =
        favoritesApi.listenForUserFavorites()
            .flowOn(Dispatchers.IO)
            .map {
                it.map { favorite -> PetFavorite(favorite.id, favorite.mapToPet()) }
            }

    private suspend fun Favorite.mapToPet() = requestNonNull {
        petApi.getPet(info.petId)
    }

    fun logout() = profileApi.logout()
}