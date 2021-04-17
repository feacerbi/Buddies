package com.buddies.gallery.viewstate

import com.buddies.common.viewstate.ViewState
import com.buddies.gallery.R
import com.buddies.server.model.Picture

data class GalleryViewState (
    val toolbarMenu: Int = R.menu.empty_menu,
    val picturesList: List<Picture> = listOf(),
    val showEmpty: Boolean = true,
    val showLoading: Boolean = false
) : ViewState