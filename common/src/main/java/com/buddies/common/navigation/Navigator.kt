package com.buddies.common.navigation

import androidx.fragment.app.Fragment

interface Navigator {

    fun steer(currentFragment: Fragment, direction: NavDirection)
    fun back(currentFragment: Fragment)

    sealed class NavDirection {
        object SplashToLogin : NavDirection()
        object SplashToProfile : NavDirection()

        object LoginToProfile : NavDirection()

        object ProfileToLogin : NavDirection()
        data class ProfileToPetProfile(val petId: String) : NavDirection()

        object TagScanToAnimalAndBreed : NavDirection()
        object AnimalAndBreedToInfo : NavDirection()
        object InfoToConfirmation : NavDirection()
    }
}