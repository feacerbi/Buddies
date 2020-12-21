package com.buddies.gallery.viewstate

import android.net.Uri
import com.buddies.common.viewstate.ViewState

data class GalleryViewState (
    var picturesList: List<Uri> = listOf(),
    var showEmpty: Boolean = false
) : ViewState