package com.buddies.common.ui

import androidx.fragment.app.Fragment
import com.buddies.common.navigation.Navigator
import com.buddies.common.navigation.Navigator.NavDirection
import org.koin.android.ext.android.inject

abstract class NavigationFragment : Fragment() {

    open val navigator: Navigator by inject()

    protected fun navigate(direction: NavDirection) {
        navigator.steer(this, direction)
    }

    protected fun navigateBack() {
        navigator.back(this)
    }
}