package com.buddies.profile.viewstate

import android.net.Uri
import androidx.paging.PagingData
import com.buddies.common.model.PetFavorite
import com.buddies.common.model.UserNotification
import com.buddies.common.viewstate.ViewState

data class ProfileViewState(
    val name: String = "",
    val email: String = "",
    val photo: Uri = Uri.EMPTY,
    val notifications: List<UserNotification> = listOf(),
    val favorites: PagingData<PetFavorite> = PagingData.empty(),
    val emptyNotifications: Boolean = true,
    val loadingInfo: Boolean = false,
    val loadingNotifications: Boolean = false,
    val loadingFavorites: Boolean = false,
    val showFavorites: Boolean = false,
    val showMyPets: Boolean = false
) : ViewState