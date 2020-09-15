package com.buddies.home.viewstate

import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.viewstate.ViewEffect

sealed class HomeViewEffect : ViewEffect {
    object StartCamera : HomeViewEffect()
    object StopCamera : HomeViewEffect()
    data class ShowMessage(val message: Int) : HomeViewEffect()
    data class Navigate(val direction: NavDirection) : HomeViewEffect()
    data class ShowError(val error: Int) : HomeViewEffect()
}