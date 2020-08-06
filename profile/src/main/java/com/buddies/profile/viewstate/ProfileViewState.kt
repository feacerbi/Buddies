package com.buddies.profile.viewstate

import android.net.Uri
import com.buddies.common.viewstate.ViewState

class ProfileViewState(
    var name: String = "",
    var email: String = "",
    var photo: Uri = Uri.EMPTY,
    var myPetsWidgetExpanded: Boolean = false,
    var loading: Boolean = false
) : ViewState