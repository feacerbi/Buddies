package com.buddies.mypets.viewstate

import android.net.Uri
import com.buddies.common.viewstate.ViewState

class PetProfileViewState(
    var name: String = "",
    var tag: String = "",
    var animal: String = "",
    var breed: String = "",
    var photo: Uri = Uri.EMPTY
) : ViewState