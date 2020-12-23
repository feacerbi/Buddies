package com.buddies.home.viewstate

import androidx.annotation.StringRes
import com.buddies.common.model.UserInfo
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.viewstate.ViewEffect

sealed class HomeViewEffect : ViewEffect {
    object StartCamera : HomeViewEffect()
    object StopCamera : HomeViewEffect()
    data class ShowShareInfoDialog(val user: UserInfo) : HomeViewEffect()
    data class ShowMessage(@StringRes val message: Int) : HomeViewEffect()
    data class Navigate(val direction: NavDirection) : HomeViewEffect()
    data class ShowError(@StringRes val error: Int) : HomeViewEffect()
}