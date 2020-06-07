package com.buddies.navigation.navigator

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.buddies.common.navigation.Navigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.NavDirection.*
import com.buddies.login.ui.LoginFragmentDirections.Companion.actionLoginFragmentToProfileFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToLoginFragment
import com.buddies.splash.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToLoginFragment
import com.buddies.splash.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToProfileFragment

class AppNavigator : Navigator {

    override fun steer(currentFragment: Fragment, direction: NavDirection) {
        currentFragment.findNavController().navigate(direction.action())
    }

    override fun back(currentFragment: Fragment) {
        currentFragment.findNavController().popBackStack()
    }

    private fun NavDirection.action(): NavDirections = when (this) {
        SPLASH_TO_LOGIN -> actionSplashScreenFragmentToLoginFragment()
        SPLASH_TO_PROFILE -> actionSplashScreenFragmentToProfileFragment()

        LOGIN_TO_PROFILE -> actionLoginFragmentToProfileFragment()

        PROFILE_TO_LOGIN -> actionProfileFragmentToLoginFragment()
    }
}