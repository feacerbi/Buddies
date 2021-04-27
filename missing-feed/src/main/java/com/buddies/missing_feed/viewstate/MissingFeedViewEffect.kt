package com.buddies.missing_feed.viewstate

import androidx.annotation.StringRes
import com.buddies.common.navigation.Navigator
import com.buddies.common.viewstate.ViewEffect

sealed class MissingFeedViewEffect : ViewEffect {
    data class ShowMessage(@StringRes val message: Int) : MissingFeedViewEffect()
    data class Navigate(val direction: Navigator.NavDirection) : MissingFeedViewEffect()
    data class ShowError(@StringRes val error: Int) : MissingFeedViewEffect()
}
