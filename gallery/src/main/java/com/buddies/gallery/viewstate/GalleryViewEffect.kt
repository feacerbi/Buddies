package com.buddies.gallery.viewstate

import androidx.annotation.StringRes
import com.buddies.common.navigation.Navigator
import com.buddies.common.viewstate.ViewEffect

sealed class GalleryViewEffect : ViewEffect {
    data class OpenConfirmDeleteDialog(val pictureIds: List<String>) : GalleryViewEffect()
    data class ShowMessage(@StringRes val message: Int) : GalleryViewEffect()
    data class Navigate(val direction: Navigator.NavDirection) : GalleryViewEffect()
    data class ShowError(val error: Int) : GalleryViewEffect()
}