package com.buddies.profile.viewstate

import android.net.Uri
import androidx.core.net.toUri
import com.buddies.common.model.User
import com.buddies.common.viewstate.ViewStateReducer

sealed class ProfileViewStateReducer : ViewStateReducer<ProfileViewState> {

    data class ShowInfo(val user: User?) : ProfileViewStateReducer() {
        override val reduce: ProfileViewState.() -> Unit = {
            name = user?.info?.name ?: ""
            email = user?.info?.email ?: ""
            photo = user?.info?.photo?.toUri() ?: Uri.EMPTY
        }
    }

}