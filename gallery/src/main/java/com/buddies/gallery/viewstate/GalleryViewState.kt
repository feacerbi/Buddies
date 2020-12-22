package com.buddies.gallery.viewstate

import com.buddies.common.viewstate.ViewState
import com.buddies.server.model.Picture

data class GalleryViewState (
    var picturesList: List<Picture> = listOf(),
    var showEmpty: Boolean = false,
    var showloading: Boolean = false
) : ViewState