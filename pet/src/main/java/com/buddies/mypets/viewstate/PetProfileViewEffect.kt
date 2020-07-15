package com.buddies.mypets.viewstate

import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.viewstate.ViewEffect

sealed class PetProfileViewEffect : ViewEffect {
    data class Navigate(val direction: NavDirection) : PetProfileViewEffect()
    data class ShowError(val error: Int) : PetProfileViewEffect()
}