package com.buddies.profile.viewstate

import android.net.Uri
import com.buddies.common.model.User
import com.buddies.common.viewstate.ViewStateReducer

sealed class ProfileViewStateReducer : ViewStateReducer<ProfileViewState> {

    data class ShowInfo(val user: User?) : ProfileViewStateReducer() {
        override val reduce: ProfileViewState.() -> Unit = {
            name = user?.name ?: ""
            email = user?.email ?: ""
            photo = user?.photo ?: Uri.EMPTY
        }
    }

}