package com.buddies.profile.viewstate

import android.net.Uri
import com.buddies.common.viewstate.ViewState

class ProfileViewState(
    var name: String = "",
    var email: String = "",
    var photo: Uri = Uri.EMPTY
) : ViewState