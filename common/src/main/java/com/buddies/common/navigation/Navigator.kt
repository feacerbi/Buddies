package com.buddies.common.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator

interface Navigator {

    fun steer(currentFragment: Fragment, direction: NavDirection)
    fun steer(currentFragment: Fragment, direction: NavDirection, extras: FragmentNavigator.Extras)
    fun back(currentFragment: Fragment)

    open class NavDirection {
        object SplashToLogin : NavDirection()
        object SplashToHome : NavDirection()
        object SplashToMissingFeed : NavDirection()

        object LoginToHome : NavDirection()
        object LoginToMissingFeed : NavDirection()

        object HomeToProfile : NavDirection()
        data class HomeToPetProfile(val petId: String) : NavDirection()
        data class HomeToNewPetFlow(val tagValue: String) : NavDirection()

        object MissingFeedToProfile : NavDirection()
        object MissingFeedToNewPetFlow : NavDirection()

        object ProfileToLogin : NavDirection()
        object ProfileToNewPetFlow : NavDirection()
        data class ProfileToPetProfile(val petId: String) : NavDirection()
        data class ProfileToFullscreen(
            val pictureUrl: String,
            val transitionName: String) : NavDirection()

        data class PetProfileToGallery(val petId: String) : NavDirection()
        data class PetProfileToFullscreen(
            val pictureUrl: String,
            val transitionName: String) : NavDirection()

        data class GalleryToFullscreen(
            val pictureUrl: String,
            val transitionName: String) : NavDirection()
    }

    class UnsupportedDirectionException : Exception()
}