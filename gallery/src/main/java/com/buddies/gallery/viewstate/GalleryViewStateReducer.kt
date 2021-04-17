package com.buddies.gallery.viewstate

import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.gallery.R
import com.buddies.server.model.Picture

sealed class GalleryViewStateReducer : ViewStateReducer<GalleryViewState> {

    data class ShowPictures(
        val pictures: List<Picture>?,
        val editEnabled: Boolean?
    ) : GalleryViewStateReducer() {
        override fun reduce(state: GalleryViewState) = state.copy(
            toolbarMenu = if (editEnabled == true) R.menu.gallery_toolbar_menu else R.menu.empty_menu,
            picturesList = pictures ?: listOf(),
            showEmpty = pictures.isNullOrEmpty(),
            showLoading = false
        )
    }

    object ShowLoading : GalleryViewStateReducer() {
        override fun reduce(state: GalleryViewState) = state.copy(
            showLoading = true
        )
    }
}