package com.buddies.navigation.navigator

import androidx.navigation.NavDirections
import com.buddies.common.navigation.BaseNavigator
import com.buddies.common.navigation.Navigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.NavDirection.*
import com.buddies.home.ui.HomeFragmentDirections.Companion.actionHomeFragmentToProfileFragment
import com.buddies.login.ui.LoginFragmentDirections.Companion.actionLoginFragmentToHomeFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToHomeFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToLoginFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToLoginFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToNewPetFlow
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToPetProfileFragment

class AppNavigator : BaseNavigator() {

    override fun NavDirection.action(): NavDirections = when (this) {
        is SplashToLogin -> actionSplashScreenFragmentToLoginFragment()
        is SplashToHome -> actionSplashScreenFragmentToHomeFragment()

        is LoginToHome -> actionLoginFragmentToHomeFragment()

        is HomeToProfile -> actionHomeFragmentToProfileFragment()

        is ProfileToLogin -> actionProfileFragmentToLoginFragment()
        is ProfileToNewPetFlow -> actionProfileFragmentToNewPetFlow()
        is ProfileToPetProfile -> actionProfileFragmentToPetProfileFragment(petId)

        else -> throw Navigator.UnsupportedDirectionException()
    }
}