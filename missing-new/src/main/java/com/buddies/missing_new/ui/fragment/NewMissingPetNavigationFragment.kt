package com.buddies.missing_new.ui.fragment

import com.buddies.common.navigation.Navigator
import com.buddies.common.ui.fragment.NavigationFragment
import com.buddies.missing_new.navigation.NewMissingPetNavigator.Companion.NEW_MISSING_PET_NAVIGATOR_NAME
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

abstract class NewMissingPetNavigationFragment : NavigationFragment() {
    override val navigator: Navigator by inject(named(NEW_MISSING_PET_NAVIGATOR_NAME))
}