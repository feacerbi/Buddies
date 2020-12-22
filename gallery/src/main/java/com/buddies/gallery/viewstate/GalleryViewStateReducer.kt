package com.buddies.gallery.viewstate

import com.buddies.common.viewstate.ViewStateReducer
import com.buddies.server.model.Picture

sealed class GalleryViewStateReducer : ViewStateReducer<GalleryViewState> {

    data class ShowPictures(
        val pictures: List<Picture>?
    ) : GalleryViewStateReducer() {
        override val reduce: GalleryViewState.() -> Unit = {
            picturesList = pictures ?: listOf()
            showEmpty = picturesList.isEmpty()
            showloading = false
        }
    }

    object ShowLoading : GalleryViewStateReducer() {
        override val reduce: GalleryViewState.() -> Unit = {
            showloading = true
        }
    }
}