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
        override val reduce: ProfileViewState.() -> Unit = {
            name = user?.info?.name ?: ""
            email = user?.info?.email ?: ""
            photo = user?.info?.photo?.toUri() ?: Uri.EMPTY
            loadingInfo = false
        }
    }

    data class ShowNotifications(
        val list: List<UserNotification>?
    ) : ProfileViewStateReducer() {
        override val reduce: ProfileViewState.() -> Unit = {
            notifications = list ?: listOf()
            emptyNotifications = notifications.isEmpty()
            loadingNotifications = false
        }
    }

    data class NotificationRemoved(
        val notification: UserNotification
    ) : ProfileViewStateReducer() {
        override val reduce: ProfileViewState.() -> Unit = {
            notifications = notifications.minus(notification)
            emptyNotifications = notifications.isEmpty()
        }
    }

    object InfoLoading : ProfileViewStateReducer() {
        override val reduce: ProfileViewState.() -> Unit = {
            loadingInfo = true
        }
    }

    object NotificationsLoading : ProfileViewStateReducer() {
        override val reduce: ProfileViewState.() -> Unit = {
            loadingNotifications = true
        }
    }

    object ShowError : ProfileViewStateReducer() {
        override val reduce: ProfileViewState.() -> Unit = {
            loadingInfo = false
            loadingNotifications = false
            emptyNotifications = notifications.isEmpty()
        }
    }
}