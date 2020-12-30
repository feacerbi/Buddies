package com.buddies.common.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.buddies.common.navigation.Navigator
import com.buddies.common.navigation.Navigator.NavDirection
import org.koin.android.ext.android.inject

abstract class NavigationFragment : Fragment() {

    open val navigator: Navigator by inject()

    protected fun navigate(direction: NavDirection) {
        navigator.steer(this, direction)
    }

    protected fun navigate(direction: NavDirection, vararg sharedElements: Pair<View, String>) {
        navigator.steer(this, direction, FragmentNavigatorExtras(*sharedElements))
    }

    protected fun navigateBack() {
        navigator.back(this)
    }
}