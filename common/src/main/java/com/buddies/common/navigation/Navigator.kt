package com.buddies.common.navigation

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.buddies.common.R

interface Navigator {

    fun steer(currentFragment: Fragment, direction: NavDirection)
    fun back(currentFragment: Fragment)

    enum class NavDirection(@StringRes val title: Int) {
        SPLASH_TO_LOGIN(R.string.login_title),
        SPLASH_TO_PROFILE(R.string.profile_title),
        LOGIN_TO_PROFILE(R.string.profile_title),
        PROFILE_TO_LOGIN(R.string.login_title)
    }
}