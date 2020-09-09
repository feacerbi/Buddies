package com.buddies.common.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

abstract class BaseNavigator : Navigator {

    override fun steer(currentFragment: Fragment, direction: Navigator.NavDirection) {
        currentFragment.findNavController().navigate(direction.action())
    }

    override fun back(currentFragment: Fragment) {
        currentFragment.findNavController().popBackStack()
    }

    abstract fun Navigator.NavDirection.action(): NavDirections
}