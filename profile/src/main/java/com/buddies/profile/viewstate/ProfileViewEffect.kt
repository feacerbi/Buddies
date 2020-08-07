package com.buddies.profile.viewstate

import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.viewstate.ViewEffect

sealed class ProfileViewEffect : ViewEffect {
    object RefreshPets : ProfileViewEffect()
    data class Navigate(val direction: NavDirection) : ProfileViewEffect()
    data class ShowError(val error: Int) : ProfileViewEffect()
}