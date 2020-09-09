package com.buddies.navigation.navigator

import androidx.navigation.NavDirections
import com.buddies.common.navigation.BaseNavigator
import com.buddies.common.navigation.Navigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.NavDirection.*
import com.buddies.login.ui.LoginFragmentDirections.Companion.actionLoginFragmentToProfileFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToLoginFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToProfileFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToLoginFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToNewPetFlow
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToPetProfileFragment

class AppNavigator : BaseNavigator() {

    override fun NavDirection.action(): NavDirections = when (this) {
        is SplashToLogin -> actionSplashScreenFragmentToLoginFragment()
        is SplashToProfile -> actionSplashScreenFragmentToProfileFragment()

        is LoginToProfile -> actionLoginFragmentToProfileFragment()

        is ProfileToLogin -> actionProfileFragmentToLoginFragment()
        is ProfileToNewPetFlow -> actionProfileFragmentToNewPetFlow()
        is ProfileToPetProfile -> actionProfileFragmentToPetProfileFragment(petId)

        else -> throw Navigator.UnsupportedDirectionException()
    }
}