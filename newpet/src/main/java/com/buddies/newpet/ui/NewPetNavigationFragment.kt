package com.buddies.newpet.ui

import com.buddies.common.navigation.Navigator
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.newpet.navigation.NewPetNavigator
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

abstract class NewPetNavigationFragment : NavigationFragment() {
    override val navigator: Navigator by inject(named(NewPetNavigator.NEW_PET_NAVIGATOR_NAME))
}