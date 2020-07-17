package com.buddies.navigation.navigator

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.buddies.common.navigation.Navigator
import com.buddies.common.navigation.Navigator.NavDirection
import com.buddies.common.navigation.Navigator.NavDirection.*
import com.buddies.login.ui.LoginFragmentDirections.Companion.actionLoginFragmentToProfileFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToLoginFragment
import com.buddies.login.ui.SplashScreenFragmentDirections.Companion.actionSplashScreenFragmentToProfileFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToLoginFragment
import com.buddies.profile.ui.ProfileFragmentDirections.Companion.actionProfileFragmentToPetProfileFragment

class AppNavigator : Navigator {

    override fun steer(currentFragment: Fragment, direction: NavDirection) {
        currentFragment.findNavController().navigate(direction.action())
    }

    override fun back(currentFragment: Fragment) {
        currentFragment.findNavController().popBackStack()
    }

    private fun NavDirection.action(): NavDirections = when (this) {
        is SplashToLogin -> actionSplashScreenFragmentToLoginFragment()
        is SplashToProfile -> actionSplashScreenFragmentToProfileFragment()

        is LoginToProfile -> actionLoginFragmentToProfileFragment()

        is ProfileToLogin -> actionProfileFragmentToLoginFragment()
        is ProfileToPetProfile -> actionProfileFragmentToPetProfileFragment(petId)
    }
}