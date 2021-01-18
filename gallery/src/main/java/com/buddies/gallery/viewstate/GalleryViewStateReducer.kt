package com.buddies.gallery.viewstate

import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.server.model.Picture

sealed class GalleryViewStateReducer : ViewStateReducer<GalleryViewState> {

    data class ShowPictures(
        val pictures: List<Picture>?
    ) : GalleryViewStateReducer() {
        override fun reduce(state: GalleryViewState) = state.copy(
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