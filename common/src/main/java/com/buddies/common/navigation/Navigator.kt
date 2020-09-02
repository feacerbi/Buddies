package com.buddies.common.navigation

import androidx.fragment.app.Fragment

interface Navigator {

    fun steer(currentFragment: Fragment, direction: NavDirection)
    fun back(currentFragment: Fragment)

    open class NavDirection {
        object SplashToLogin : NavDirection()
        object SplashToProfile : NavDirection()

        object LoginToProfile : NavDirection()

        object ProfileToLogin : NavDirection()
        object ProfileToNewPetFlow : NavDirection()
        data class ProfileToPetProfile(val petId: String) : NavDirection()
    }

    class UnsupportedDirectionException : Exception()
}