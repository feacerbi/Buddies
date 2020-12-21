package com.buddies.gallery.viewstate

import com.buddies.common.navigation.Navigator
import com.buddies.common.viewstate.ViewEffect

sealed class GalleryViewEffect : ViewEffect {
    data class Navigate(val direction: Navigator.NavDirection) : GalleryViewEffect()
    data class ShowError(val error: Int) : GalleryViewEffect()
}