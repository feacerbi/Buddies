package com.buddies.profile.viewstate

import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.viewstate.ViewEffect
import com.buddies.profile.model.ContactInfo

sealed class ProfileViewEffect : ViewEffect {
    object RefreshPets : ProfileViewEffect()
    data class ShowContactInfoBottomSheet(val info: List<ContactInfo>) : ProfileViewEffect()
    data class Navigate(val direction: NavDirection) : ProfileViewEffect()
    data class ShowError(val error: Int) : ProfileViewEffect()
}