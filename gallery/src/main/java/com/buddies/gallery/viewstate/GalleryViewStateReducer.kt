package com.buddies.gallery.viewstate

import android.net.Uri
import com.buddies.common.viewstate.ViewStateReducer

sealed class GalleryViewStateReducer : ViewStateReducer<GalleryViewState> {

    data class ShowPictures(
        val pictures: List<Uri>?
    ) : GalleryViewStateReducer() {
        override val reduce: GalleryViewState.() -> Unit = {
            picturesList = pictures ?: listOf()
            showEmpty = picturesList.isEmpty()
        }
    }
}