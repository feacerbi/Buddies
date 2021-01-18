package com.buddies.gallery.viewstate

import com.buddies.common.viewstate.ViewState
import com.buddies.server.model.Picture

data class GalleryViewState (
    val picturesList: List<Picture> = listOf(),
    val showEmpty: Boolean = true,
    val showLoading: Boolean = false
) : ViewState