package com.buddies.profile.viewstate

import android.net.Uri
import com.buddies.common.model.UserNotification
import com.buddies.common.viewstate.ViewState

class ProfileViewState(
    var name: String = "",
    var email: String = "",
    var photo: Uri = Uri.EMPTY,
    var myPetsWidgetExpanded: Boolean = false,
    var notifications: List<UserNotification> = listOf(),
    var emptyNotifications: Boolean = false,
    var loadingInfo: Boolean = false,
    var loadingNotifications: Boolean = false
) : ViewState