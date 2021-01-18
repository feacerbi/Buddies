package com.buddies.profile.viewstate

import android.net.Uri
import androidx.core.net.toUri
import com.buddies.common.model.User
import com.buddies.common.model.UserNotification
import com.buddies.common.viewstate.ViewStateReducer

sealed class ProfileViewStateReducer : ViewStateReducer<ProfileViewState> {

    data class ShowInfo(
        val user: User?
    ) : ProfileViewStateReducer() {
        override fun reduce(state: ProfileViewState) = state.copy(
            name = user?.info?.name ?: "",
            email = user?.info?.email ?: "",
            photo = user?.info?.photo?.toUri() ?: Uri.EMPTY,
            loadingInfo = false
        )
    }

    data class ShowNotifications(
        val list: List<UserNotification>?
    ) : ProfileViewStateReducer() {
        override fun reduce(state: ProfileViewState) = state.copy(
            notifications = list ?: listOf(),
            emptyNotifications = list.isNullOrEmpty(),
            loadingNotifications = false
        )
    }

    data class NotificationRemoved(
        val notification: UserNotification
    ) : ProfileViewStateReducer() {
        override fun reduce(state: ProfileViewState) = state.copy(
            notifications = state.notifications.minus(notification),
            emptyNotifications = state.notifications.minus(notification).isEmpty()
        )
    }

    object InfoLoading : ProfileViewStateReducer() {
        override fun reduce(state: ProfileViewState) = state.copy(
            loadingInfo = true
        )
    }

    object NotificationsLoading : ProfileViewStateReducer() {
        override fun reduce(state: ProfileViewState) = state.copy(
            loadingNotifications = true
        )
    }

    object ShowError : ProfileViewStateReducer() {
        override fun reduce(state: ProfileViewState) = state.copy(
            loadingInfo = false,
            loadingNotifications = false
        )
    }
}